"""
Database query validators for security and data integrity.
"""
from typing import Dict, List
from app.utils.errors import ValidationError

# Whitelist of allowed sort fields for each entity
ALLOWED_SORT_FIELDS = {
    "application": [
        "id", "status", "position", "work_setting", "location", 
        "compensation", "created_on", "modified_on", "company_id", "client_id"
    ],
    "company": [
        "id", "name", "city", "state", "country", "job_type",
        "created_on", "modified_on"
    ],
    "client": [
        "id", "name", "created_on", "modified_on"
    ],
    "contact": [
        "id", "first_name", "last_name", "title", "contact_type", "company_id", 
        "application_id", "client_id", "created_on", "modified_on"
    ],
    "note": [
        "id", "application_id", "created_on", "modified_on"
    ],
    "job_search_site": [
        "id", "name", "created_on", "modified_on"
    ],
}


def validate_sort_field(entity: str, sort_field: str) -> str:
    """
    Validate that sort field is in the whitelist for the entity.
    
    Args:
        entity: Entity name (e.g., "application", "company")
        sort_field: Sort field to validate
        
    Returns:
        Validated sort field
        
    Raises:
        ValidationError: If sort field is not allowed
    """
    allowed_fields = ALLOWED_SORT_FIELDS.get(entity, [])
    
    if sort_field not in allowed_fields:
        raise ValidationError(
            f"Invalid sort field '{sort_field}'. Allowed fields: {', '.join(allowed_fields)}",
            field="sort"
        )
    
    return sort_field
