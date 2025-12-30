"""
Database query functions for CRUD operations.
"""
from typing import List, Optional, Dict, Any
from datetime import datetime
import sqlite3
from app.database.connection import get_db_connection
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ValidationError, ConflictError


def _row_to_dict(row: sqlite3.Row) -> Dict[str, Any]:
    """Convert sqlite3.Row to dictionary."""
    return dict(row)


def _build_where_clause(filters: Dict[str, Any], include_deleted: bool = False) -> tuple[str, List[Any]]:
    """Build WHERE clause from filters."""
    conditions = []
    values = []
    
    if not include_deleted:
        conditions.append("is_deleted = 0")
    
    for key, value in filters.items():
        if value is not None:
            conditions.append(f"{key} = ?")
            values.append(value)
    
    where_clause = " AND ".join(conditions) if conditions else "1=1"
    return where_clause, values


# ============================================================================
# APPLICATION QUERIES
# ============================================================================

def create_application(data: Dict[str, Any]) -> Dict[str, Any]:
    """Create a new application."""
    with get_db_connection() as conn:
        cursor = conn.execute("""
            INSERT INTO application (
                status, requirement, work_setting, compensation, position,
                job_description, job_link, location, resume, cover_letter,
                entered_iwd, date_close, company_id, client_id,
                created_by, modified_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            data.get("status", "Pending"),
            data.get("requirement"),
            data.get("work_setting", "Remote"),
            data.get("compensation"),
            data.get("position"),
            data.get("job_description"),
            data.get("job_link"),
            data.get("location"),
            data.get("resume"),
            data.get("cover_letter"),
            data.get("entered_iwd", 0),
            data.get("date_close"),
            data.get("company_id"),
            data.get("client_id"),
            data["created_by"],
            data["modified_by"],
        ))
        application_id = cursor.lastrowid
        conn.commit()
        
        return get_application_by_id(application_id)


def get_application_by_id(application_id: int, include_deleted: bool = False) -> Optional[Dict[str, Any]]:
    """Get application by ID with related entities."""
    with get_db_connection() as conn:
        where_clause = "a.id = ?"
        if not include_deleted:
            where_clause += " AND a.is_deleted = 0"
        
        cursor = conn.execute(f"""
            SELECT 
                a.*,
                c.name AS company_name,
                c.address AS company_address,
                c.city AS company_city,
                c.state AS company_state,
                c.zip AS company_zip,
                c.country AS company_country,
                cl.name AS client_name
            FROM application a
            LEFT JOIN company c ON a.company_id = c.id AND c.is_deleted = 0
            LEFT JOIN client cl ON a.client_id = cl.id AND cl.is_deleted = 0
            WHERE {where_clause}
            LIMIT 1
        """, (application_id,))
        
        row = cursor.fetchone()
        if not row:
            return None
        
        app_data = _row_to_dict(row)
        
        # Get all contacts for this application with emails and phones
        contacts_cursor = conn.execute("""
            SELECT DISTINCT con.id
            FROM contact con
            WHERE con.application_id = ? AND con.is_deleted = 0
        """, (application_id,))
        
        contact_ids = [row[0] for row in contacts_cursor.fetchall()]
        contacts = []
        
        for contact_id in contact_ids:
            # Get contact details
            contact_cursor = conn.execute("""
                SELECT 
                    *,
                    first_name || ' ' || last_name AS name
                FROM contact WHERE id = ? AND is_deleted = 0
            """, (contact_id,))
            contact_row = contact_cursor.fetchone()
            if contact_row:
                contact_dict = _row_to_dict(contact_row)
                
                # Get emails
                email_cursor = conn.execute("""
                    SELECT * FROM contact_email 
                    WHERE contact_id = ? AND is_deleted = 0
                    ORDER BY is_primary DESC
                """, (contact_id,))
                contact_dict['emails'] = [_row_to_dict(row) for row in email_cursor.fetchall()]
                
                # Get phones
                phone_cursor = conn.execute("""
                    SELECT * FROM contact_phone 
                    WHERE contact_id = ? AND is_deleted = 0
                    ORDER BY is_primary DESC
                """, (contact_id,))
                contact_dict['phones'] = [_row_to_dict(row) for row in phone_cursor.fetchall()]
                
                contacts.append(contact_dict)
        
        app_data['contacts'] = contacts
        
        return app_data


def list_applications(
    page: int = 1,
    limit: int = 50,
    status: Optional[str] = None,
    company_id: Optional[int] = None,
    client_id: Optional[int] = None,
    sort: str = "created_on",
    order: str = "desc",
    include_deleted: bool = False
) -> Dict[str, Any]:
    """List applications with pagination and filtering, including related entity names."""
    filters = {}
    if status:
        filters["a.status"] = status
    if company_id:
        filters["a.company_id"] = company_id
    if client_id:
        filters["a.client_id"] = client_id
    
    # Build WHERE clause with table alias
    conditions = []
    values = []
    
    if not include_deleted:
        conditions.append("a.is_deleted = 0")
    
    for key, value in filters.items():
        if value is not None:
            conditions.append(f"{key} = ?")
            values.append(value)
    
    where_clause = " AND ".join(conditions) if conditions else "1=1"
    
    with get_db_connection() as conn:
        # Get total count
        count_cursor = conn.execute(f"""
            SELECT COUNT(*) as total 
            FROM application a
            WHERE {where_clause}
        """, values)
        total = count_cursor.fetchone()[0]
        
        # Get paginated data with related entity names
        # Validate sort field to prevent SQL injection (defense in depth)
        validated_sort = validate_sort_field("application", sort)
        offset = (page - 1) * limit
        
        data_cursor = conn.execute(f"""
            SELECT 
                a.*,
                c.name AS company_name,
                cl.name AS client_name,
                (SELECT first_name || ' ' || last_name FROM contact WHERE application_id = a.id AND is_deleted = 0 LIMIT 1) AS contact_name
            FROM application a
            LEFT JOIN company c ON a.company_id = c.id AND c.is_deleted = 0
            LEFT JOIN client cl ON a.client_id = cl.id AND cl.is_deleted = 0
            WHERE {where_clause}
            ORDER BY a.{validated_sort} {order.upper()}
            LIMIT ? OFFSET ?
        """, values + [limit, offset])
        
        applications = [_row_to_dict(row) for row in data_cursor.fetchall()]
        
        return {
            "data": applications,
            "pagination": {
                "page": page,
                "limit": limit,
                "total": total,
                "pages": (total + limit - 1) // limit
            }
        }


def update_application(application_id: int, data: Dict[str, Any]) -> Dict[str, Any]:
    """Update an application."""
    existing = get_application_by_id(application_id)
    if not existing:
        raise NotFoundError("Application", application_id)
    
    updates = []
    values = []
    
    updatable_fields = [
        "status", "requirement", "work_setting", "compensation", "position",
        "job_description", "job_link", "location", "resume", "cover_letter",
        "entered_iwd", "date_close", "company_id", "client_id"
    ]
    
    for field in updatable_fields:
        if field in data and data[field] is not None:
            updates.append(f"{field} = ?")
            values.append(data[field])
    
    # Always update audit fields, even if no other fields changed
    # This ensures audit trail is maintained
    updates.append("modified_by = ?")
    updates.append("modified_on = datetime('now', 'localtime')")
    values.append(data["modified_by"])
    values.append(application_id)
    
    with get_db_connection() as conn:
        conn.execute(f"""
            UPDATE application 
            SET {', '.join(updates)}
            WHERE id = ?
        """, values)
        conn.commit()
    
    return get_application_by_id(application_id)


def delete_application(application_id: int) -> None:
    """Delete an application (hard delete with cascading)."""
    existing = get_application_by_id(application_id, include_deleted=True)
    if not existing:
        raise NotFoundError("Application", application_id)
    
    with get_db_connection() as conn:
        conn.execute("DELETE FROM application WHERE id = ?", (application_id,))
        conn.commit()


# ============================================================================
# COMPANY QUERIES
# ============================================================================

def create_company(data: Dict[str, Any]) -> Dict[str, Any]:
    """Create a new company."""
    with get_db_connection() as conn:
        cursor = conn.execute("""
            INSERT INTO company (
                name, address, city, state, zip, country, job_type,
                created_by, modified_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            data["name"],
            data.get("address"),
            data.get("city"),
            data.get("state"),
            data.get("zip"),
            data.get("country", "United States"),
            data.get("job_type", "Technology"),
            data["created_by"],
            data["modified_by"],
        ))
        company_id = cursor.lastrowid
        conn.commit()
        
        return get_company_by_id(company_id)


def get_company_by_id(company_id: int, include_deleted: bool = False) -> Optional[Dict[str, Any]]:
    """Get company by ID."""
    with get_db_connection() as conn:
        where_clause = "id = ?"
        if not include_deleted:
            where_clause += " AND is_deleted = 0"
        
        cursor = conn.execute(f"""
            SELECT * FROM company WHERE {where_clause}
        """, (company_id,))
        
        row = cursor.fetchone()
        return _row_to_dict(row) if row else None


def list_companies(
    page: int = 1,
    limit: int = 50,
    job_type: Optional[str] = None,
    sort: str = "created_on",
    order: str = "desc",
    include_deleted: bool = False
) -> Dict[str, Any]:
    """List companies with pagination and filtering."""
    filters = {}
    if job_type:
        filters["job_type"] = job_type
    
    where_clause, where_values = _build_where_clause(filters, include_deleted)
    
    with get_db_connection() as conn:
        count_cursor = conn.execute(f"""
            SELECT COUNT(*) as total FROM company WHERE {where_clause}
        """, where_values)
        total = count_cursor.fetchone()[0]
        
        # Validate sort field to prevent SQL injection (defense in depth)
        validated_sort = validate_sort_field("company", sort)
        offset = (page - 1) * limit
        data_cursor = conn.execute(f"""
            SELECT * FROM company 
            WHERE {where_clause}
            ORDER BY {validated_sort} {order.upper()}
            LIMIT ? OFFSET ?
        """, where_values + [limit, offset])
        
        companies = [_row_to_dict(row) for row in data_cursor.fetchall()]
        
        return {
            "data": companies,
            "pagination": {
                "page": page,
                "limit": limit,
                "total": total,
                "pages": (total + limit - 1) // limit
            }
        }


def update_company(company_id: int, data: Dict[str, Any]) -> Dict[str, Any]:
    """Update a company."""
    existing = get_company_by_id(company_id)
    if not existing:
        raise NotFoundError("Company", company_id)
    
    updates = []
    values = []
    
    updatable_fields = ["name", "address", "city", "state", "zip", "country", "job_type"]
    
    for field in updatable_fields:
        if field in data and data[field] is not None:
            updates.append(f"{field} = ?")
            values.append(data[field])
    
    # Always update audit fields, even if no other fields changed
    # This ensures audit trail is maintained
    updates.append("modified_by = ?")
    updates.append("modified_on = datetime('now', 'localtime')")
    values.append(data["modified_by"])
    values.append(company_id)
    
    with get_db_connection() as conn:
        conn.execute(f"""
            UPDATE company 
            SET {', '.join(updates)}
            WHERE id = ?
        """, values)
        conn.commit()
    
    return get_company_by_id(company_id)


def delete_company(company_id: int) -> None:
    """Delete a company (hard delete with cascading)."""
    existing = get_company_by_id(company_id, include_deleted=True)
    if not existing:
        raise NotFoundError("Company", company_id)
    
    with get_db_connection() as conn:
        conn.execute("DELETE FROM company WHERE id = ?", (company_id,))
        conn.commit()


# ============================================================================
# CLIENT QUERIES
# ============================================================================

def create_client(data: Dict[str, Any]) -> Dict[str, Any]:
    """Create a new client."""
    with get_db_connection() as conn:
        cursor = conn.execute("""
            INSERT INTO client (name, created_by, modified_by)
            VALUES (?, ?, ?)
        """, (
            data.get("name"),
            data["created_by"],
            data["modified_by"],
        ))
        client_id = cursor.lastrowid
        conn.commit()
        
        return get_client_by_id(client_id)


def get_client_by_id(client_id: int, include_deleted: bool = False) -> Optional[Dict[str, Any]]:
    """Get client by ID."""
    with get_db_connection() as conn:
        where_clause = "id = ?"
        if not include_deleted:
            where_clause += " AND is_deleted = 0"
        
        cursor = conn.execute(f"""
            SELECT * FROM client WHERE {where_clause}
        """, (client_id,))
        
        row = cursor.fetchone()
        return _row_to_dict(row) if row else None


def list_clients(
    page: int = 1,
    limit: int = 50,
    sort: str = "created_on",
    order: str = "desc",
    include_deleted: bool = False
) -> Dict[str, Any]:
    """List clients with pagination."""
    where_clause, where_values = _build_where_clause({}, include_deleted)
    
    with get_db_connection() as conn:
        count_cursor = conn.execute(f"""
            SELECT COUNT(*) as total FROM client WHERE {where_clause}
        """, where_values)
        total = count_cursor.fetchone()[0]
        
        # Validate sort field to prevent SQL injection (defense in depth)
        validated_sort = validate_sort_field("client", sort)
        offset = (page - 1) * limit
        data_cursor = conn.execute(f"""
            SELECT * FROM client 
            WHERE {where_clause}
            ORDER BY {validated_sort} {order.upper()}
            LIMIT ? OFFSET ?
        """, where_values + [limit, offset])
        
        clients = [_row_to_dict(row) for row in data_cursor.fetchall()]
        
        return {
            "data": clients,
            "pagination": {
                "page": page,
                "limit": limit,
                "total": total,
                "pages": (total + limit - 1) // limit
            }
        }


def update_client(client_id: int, data: Dict[str, Any]) -> Dict[str, Any]:
    """Update a client."""
    existing = get_client_by_id(client_id)
    if not existing:
        raise NotFoundError("Client", client_id)
    
    updates = []
    values = []
    
    if "name" in data and data["name"] is not None:
        updates.append("name = ?")
        values.append(data["name"])
    
    # Always update audit fields, even if no other fields changed
    # This ensures audit trail is maintained
    updates.append("modified_by = ?")
    updates.append("modified_on = datetime('now', 'localtime')")
    values.append(data["modified_by"])
    values.append(client_id)
    
    with get_db_connection() as conn:
        conn.execute(f"""
            UPDATE client 
            SET {', '.join(updates)}
            WHERE id = ?
        """, values)
        conn.commit()
    
    return get_client_by_id(client_id)


def delete_client(client_id: int) -> None:
    """Delete a client (hard delete with cascading)."""
    existing = get_client_by_id(client_id, include_deleted=True)
    if not existing:
        raise NotFoundError("Client", client_id)
    
    with get_db_connection() as conn:
        conn.execute("DELETE FROM client WHERE id = ?", (client_id,))
        conn.commit()


# ============================================================================
# CONTACT QUERIES
# ============================================================================

def create_contact(data: Dict[str, Any]) -> Dict[str, Any]:
    """Create a new contact with emails and phones."""
    with get_db_connection() as conn:
        cursor = conn.execute("""
            INSERT INTO contact (
                first_name, last_name, title, linkedin, contact_type,
                company_id, application_id, client_id,
                created_by, modified_by
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            data["first_name"],
            data.get("last_name", ""),
            data.get("title", "Recruiter"),
            data.get("linkedin"),
            data["contact_type"],
            data.get("company_id"),
            data.get("application_id"),
            data.get("client_id"),
            data["created_by"],
            data["modified_by"],
        ))
        contact_id = cursor.lastrowid
        
        # Create emails
        emails = data.get("emails", [])
        for email_data in emails:
            conn.execute("""
                INSERT INTO contact_email (
                    contact_id, email, email_type, is_primary,
                    created_by, modified_by
                ) VALUES (?, ?, ?, ?, ?, ?)
            """, (
                contact_id,
                email_data["email"],
                email_data.get("email_type", "Work"),
                email_data.get("is_primary", 0),
                data["created_by"],
                data["modified_by"],
            ))
        
        # Create phones
        phones = data.get("phones", [])
        for phone_data in phones:
            conn.execute("""
                INSERT INTO contact_phone (
                    contact_id, phone, phone_type, is_primary,
                    created_by, modified_by
                ) VALUES (?, ?, ?, ?, ?, ?)
            """, (
                contact_id,
                phone_data["phone"],
                phone_data.get("phone_type", "Cell"),
                phone_data.get("is_primary", 0),
                data["created_by"],
                data["modified_by"],
            ))
        
        conn.commit()
        return get_contact_by_id(contact_id, include_related=True)


def get_contact_by_id(contact_id: int, include_deleted: bool = False, include_related: bool = False) -> Optional[Dict[str, Any]]:
    """Get contact by ID."""
    with get_db_connection() as conn:
        where_clause = "id = ?"
        if not include_deleted:
            where_clause += " AND is_deleted = 0"
        
        cursor = conn.execute(f"""
            SELECT 
                *,
                first_name || ' ' || last_name AS name
            FROM contact WHERE {where_clause}
        """, (contact_id,))
        
        row = cursor.fetchone()
        if not row:
            return None
        
        contact = _row_to_dict(row)
        
        if include_related:
            # Get emails
            email_cursor = conn.execute("""
                SELECT * FROM contact_email 
                WHERE contact_id = ? AND is_deleted = 0
                ORDER BY is_primary DESC, created_on
            """, (contact_id,))
            contact["emails"] = [_row_to_dict(row) for row in email_cursor.fetchall()]
            
            # Get phones
            phone_cursor = conn.execute("""
                SELECT * FROM contact_phone 
                WHERE contact_id = ? AND is_deleted = 0
                ORDER BY is_primary DESC, created_on
            """, (contact_id,))
            contact["phones"] = [_row_to_dict(row) for row in phone_cursor.fetchall()]
        
        return contact


def list_contacts(
    page: int = 1,
    limit: int = 50,
    company_id: Optional[int] = None,
    application_id: Optional[int] = None,
    client_id: Optional[int] = None,
    contact_type: Optional[str] = None,
    sort: str = "created_on",
    order: str = "desc",
    include_deleted: bool = False
) -> Dict[str, Any]:
    """List contacts with pagination and filtering."""
    filters = {}
    if company_id:
        filters["company_id"] = company_id
    if application_id:
        filters["application_id"] = application_id
    if client_id:
        filters["client_id"] = client_id
    if contact_type:
        filters["contact_type"] = contact_type
    
    where_clause, where_values = _build_where_clause(filters, include_deleted)
    
    with get_db_connection() as conn:
        count_cursor = conn.execute(f"""
            SELECT COUNT(*) as total FROM contact WHERE {where_clause}
        """, where_values)
        total = count_cursor.fetchone()[0]
        
        # Validate sort field to prevent SQL injection (defense in depth)
        validated_sort = validate_sort_field("contact", sort)
        offset = (page - 1) * limit
        data_cursor = conn.execute(f"""
            SELECT 
                *,
                first_name || ' ' || last_name AS name
            FROM contact 
            WHERE {where_clause}
            ORDER BY {validated_sort} {order.upper()}
            LIMIT ? OFFSET ?
        """, where_values + [limit, offset])
        
        contacts = [_row_to_dict(row) for row in data_cursor.fetchall()]
        
        return {
            "data": contacts,
            "pagination": {
                "page": page,
                "limit": limit,
                "total": total,
                "pages": (total + limit - 1) // limit
            }
        }


def update_contact(contact_id: int, data: Dict[str, Any]) -> Dict[str, Any]:
    """Update a contact."""
    existing = get_contact_by_id(contact_id)
    if not existing:
        raise NotFoundError("Contact", contact_id)
    
    updates = []
    values = []
    
    updatable_fields = ["first_name", "last_name", "title", "linkedin", "contact_type", "company_id", "application_id", "client_id"]
    
    for field in updatable_fields:
        if field in data and data[field] is not None:
            updates.append(f"{field} = ?")
            values.append(data[field])
    
    # Always update audit fields, even if no other fields changed
    # This ensures audit trail is maintained
    updates.append("modified_by = ?")
    updates.append("modified_on = datetime('now', 'localtime')")
    values.append(data["modified_by"])
    values.append(contact_id)
    
    with get_db_connection() as conn:
        conn.execute(f"""
            UPDATE contact 
            SET {', '.join(updates)}
            WHERE id = ?
        """, values)
        conn.commit()
    
    return get_contact_by_id(contact_id, include_related=True)


def delete_contact(contact_id: int) -> None:
    """Delete a contact (hard delete with cascading)."""
    existing = get_contact_by_id(contact_id, include_deleted=True)
    if not existing:
        raise NotFoundError("Contact", contact_id)
    
    with get_db_connection() as conn:
        conn.execute("DELETE FROM contact WHERE id = ?", (contact_id,))
        conn.commit()


# ============================================================================
# NOTE QUERIES
# ============================================================================

def create_note(data: Dict[str, Any]) -> Dict[str, Any]:
    """Create a new note."""
    with get_db_connection() as conn:
        cursor = conn.execute("""
            INSERT INTO note (application_id, note, created_by, modified_by)
            VALUES (?, ?, ?, ?)
        """, (
            data["application_id"],
            data["note"],
            data["created_by"],
            data["modified_by"],
        ))
        note_id = cursor.lastrowid
        conn.commit()
        
        return get_note_by_id(note_id)


def get_note_by_id(note_id: int, include_deleted: bool = False) -> Optional[Dict[str, Any]]:
    """Get note by ID."""
    with get_db_connection() as conn:
        where_clause = "id = ?"
        if not include_deleted:
            where_clause += " AND is_deleted = 0"
        
        cursor = conn.execute(f"""
            SELECT * FROM note WHERE {where_clause}
        """, (note_id,))
        
        row = cursor.fetchone()
        return _row_to_dict(row) if row else None


def list_notes(
    application_id: Optional[int] = None,
    page: int = 1,
    limit: int = 50,
    sort: str = "created_on",
    order: str = "desc",
    include_deleted: bool = False
) -> Dict[str, Any]:
    """List notes with pagination and filtering."""
    filters = {}
    if application_id:
        filters["application_id"] = application_id
    
    where_clause, where_values = _build_where_clause(filters, include_deleted)
    
    with get_db_connection() as conn:
        count_cursor = conn.execute(f"""
            SELECT COUNT(*) as total FROM note WHERE {where_clause}
        """, where_values)
        total = count_cursor.fetchone()[0]
        
        # Validate sort field to prevent SQL injection (defense in depth)
        validated_sort = validate_sort_field("note", sort)
        offset = (page - 1) * limit
        data_cursor = conn.execute(f"""
            SELECT * FROM note 
            WHERE {where_clause}
            ORDER BY {validated_sort} {order.upper()}
            LIMIT ? OFFSET ?
        """, where_values + [limit, offset])
        
        notes = [_row_to_dict(row) for row in data_cursor.fetchall()]
        
        return {
            "data": notes,
            "pagination": {
                "page": page,
                "limit": limit,
                "total": total,
                "pages": (total + limit - 1) // limit
            }
        }


def update_note(note_id: int, data: Dict[str, Any]) -> Dict[str, Any]:
    """Update a note."""
    existing = get_note_by_id(note_id)
    if not existing:
        raise NotFoundError("Note", note_id)
    
    updates = []
    values = []
    
    if "note" in data and data["note"] is not None:
        updates.append("note = ?")
        values.append(data["note"])
    
    # Always update audit fields, even if no other fields changed
    # This ensures audit trail is maintained
    updates.append("modified_by = ?")
    updates.append("modified_on = datetime('now', 'localtime')")
    values.append(data["modified_by"])
    values.append(note_id)
    
    with get_db_connection() as conn:
        conn.execute(f"""
            UPDATE note 
            SET {', '.join(updates)}
            WHERE id = ?
        """, values)
        conn.commit()
    
    return get_note_by_id(note_id)


def delete_note(note_id: int) -> None:
    """Delete a note (hard delete)."""
    existing = get_note_by_id(note_id, include_deleted=True)
    if not existing:
        raise NotFoundError("Note", note_id)
    
    with get_db_connection() as conn:
        conn.execute("DELETE FROM note WHERE id = ?", (note_id,))
        conn.commit()


# ============================================================================
# JOB SEARCH SITE QUERIES
# ============================================================================

def create_job_search_site(data: Dict[str, Any]) -> Dict[str, Any]:
    """Create a new job search site."""
    with get_db_connection() as conn:
        try:
            # Check if url column exists
            cursor = conn.execute("PRAGMA table_info(job_search_site)")
            columns = [row[1] for row in cursor.fetchall()]
            has_url_column = 'url' in columns
            
            if has_url_column and "url" in data:
                cursor = conn.execute("""
                    INSERT INTO job_search_site (site_name, url, created_by, modified_by)
                    VALUES (?, ?, ?, ?)
                """, (
                    data["name"],
                    data.get("url"),
                    data["created_by"],
                    data["modified_by"],
                ))
            else:
                cursor = conn.execute("""
                    INSERT INTO job_search_site (site_name, created_by, modified_by)
                    VALUES (?, ?, ?)
                """, (
                    data["name"],
                    data["created_by"],
                    data["modified_by"],
                ))
            site_id = cursor.lastrowid
            conn.commit()
            
            return get_job_search_site_by_id(site_id)
        except sqlite3.IntegrityError:
            raise ConflictError(
                "JobSearchSite name already exists",
                {"resource": "JobSearchSite", "field": "name", "value": data["name"]}
            )


def get_job_search_site_by_id(site_id: int, include_deleted: bool = False) -> Optional[Dict[str, Any]]:
    """Get job search site by ID."""
    with get_db_connection() as conn:
        where_clause = "id = ?"
        if not include_deleted:
            where_clause += " AND is_deleted = 0"
        
        cursor = conn.execute(f"""
            SELECT id, site_name, url, is_deleted, created_on, modified_on, created_by, modified_by
            FROM job_search_site WHERE {where_clause}
        """, (site_id,))
        
        row = cursor.fetchone()
        if not row:
            return None
        
        result = _row_to_dict(row)
        # Map site_name to name for API consistency
        if "site_name" in result:
            result["name"] = result.pop("site_name")
        return result


def list_job_search_sites(
    page: int = 1,
    limit: int = 50,
    sort: str = "created_on",
    order: str = "desc",
    include_deleted: bool = False
) -> Dict[str, Any]:
    """List job search sites with pagination."""
    where_clause, where_values = _build_where_clause({}, include_deleted)
    
    with get_db_connection() as conn:
        count_cursor = conn.execute(f"""
            SELECT COUNT(*) as total FROM job_search_site WHERE {where_clause}
        """, where_values)
        total = count_cursor.fetchone()[0]
        
        # Validate sort field to prevent SQL injection (defense in depth)
        validated_sort = validate_sort_field("job_search_site", sort)
        offset = (page - 1) * limit
        data_cursor = conn.execute(f"""
            SELECT id, site_name, url, is_deleted, created_on, modified_on, created_by, modified_by
            FROM job_search_site 
            WHERE {where_clause}
            ORDER BY {validated_sort} {order.upper()}
            LIMIT ? OFFSET ?
        """, where_values + [limit, offset])
        
        sites = []
        for row in data_cursor.fetchall():
            site = _row_to_dict(row)
            # Map site_name to name for API consistency
            if "site_name" in site:
                site["name"] = site.pop("site_name")
            sites.append(site)
        
        return {
            "data": sites,
            "pagination": {
                "page": page,
                "limit": limit,
                "total": total,
                "pages": (total + limit - 1) // limit
            }
        }


def update_job_search_site(site_id: int, data: Dict[str, Any]) -> Dict[str, Any]:
    """Update a job search site."""
    existing = get_job_search_site_by_id(site_id)
    if not existing:
        raise NotFoundError("JobSearchSite", site_id)
    
    updates = []
    values = []
    
    if "name" in data and data["name"] is not None:
        updates.append("site_name = ?")
        values.append(data["name"])
    
    if "url" in data and data["url"] is not None:
        updates.append("url = ?")
        values.append(data["url"])
    
    # Always update audit fields, even if no other fields changed
    # This ensures audit trail is maintained
    updates.append("modified_by = ?")
    updates.append("modified_on = datetime('now', 'localtime')")
    values.append(data["modified_by"])
    values.append(site_id)
    
    with get_db_connection() as conn:
        try:
            conn.execute(f"""
                UPDATE job_search_site 
                SET {', '.join(updates)}
                WHERE id = ?
            """, values)
            conn.commit()
        except sqlite3.IntegrityError:
            raise ConflictError(
                "JobSearchSite name already exists",
                {"resource": "JobSearchSite", "field": "name", "value": data["name"]}
            )
    
    return get_job_search_site_by_id(site_id)


def delete_job_search_site(site_id: int) -> None:
    """Delete a job search site (hard delete)."""
    existing = get_job_search_site_by_id(site_id, include_deleted=True)
    if not existing:
        raise NotFoundError("JobSearchSite", site_id)
    
    with get_db_connection() as conn:
        conn.execute("DELETE FROM job_search_site WHERE id = ?", (site_id,))
        conn.commit()
