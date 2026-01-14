#!/usr/bin/env python3
"""
Database Query Helper for Test Frameworks
Provides direct database access for test validation

This script can be called from Cypress, Playwright, or other test frameworks
to query the database directly, bypassing the API layer.

Usage:
    python helpers/db-query-helper.py job-search-sites --environment dev
    python helpers/db-query-helper.py job-search-sites --environment test --include-deleted
"""
import sys
import json
import sqlite3
import argparse
from pathlib import Path
from typing import Dict, Any, List, Optional

# Add project root to path to import shared config
PROJECT_ROOT = Path(__file__).parent.parent
sys.path.insert(0, str(PROJECT_ROOT))

try:
    from config.port_config import get_environment_config, get_database_config
    SHARED_CONFIG_AVAILABLE = True
except ImportError:
    SHARED_CONFIG_AVAILABLE = False
    print("Warning: Shared config not available, using defaults", file=sys.stderr)


def get_database_path(environment: str = "dev") -> Path:
    """Get database path for the given environment."""
    if not SHARED_CONFIG_AVAILABLE:
        # Fallback to default path
        db_dir = PROJECT_ROOT / "Data" / "Core"
        db_name = f"full_stack_qa_{environment}.db"
        return db_dir / db_name
    
    env_config = get_environment_config(environment)
    db_config = get_database_config()
    
    db_dir = PROJECT_ROOT / db_config["directory"]
    db_name = env_config["database"]["name"]
    
    return db_dir / db_name


def query_job_search_sites(
    environment: str = "dev",
    include_deleted: bool = False
) -> List[Dict[str, Any]]:
    """
    Query job search sites directly from the database.
    
    Args:
        environment: Environment name (dev, test, prod)
        include_deleted: Whether to include deleted sites
    
    Returns:
        List of job search site dictionaries
    """
    db_path = get_database_path(environment)
    
    if not db_path.exists():
        raise FileNotFoundError(f"Database not found: {db_path}")
    
    conn = sqlite3.connect(str(db_path))
    conn.row_factory = sqlite3.Row
    
    try:
        # Build WHERE clause
        where_clause = "1=1"
        if not include_deleted:
            where_clause += " AND is_deleted = 0"
        
        query = f"""
            SELECT id, site_name, url, is_deleted, created_on, modified_on, created_by, modified_by
            FROM job_search_site
            WHERE {where_clause}
            ORDER BY created_on DESC
        """
        
        cursor = conn.execute(query)
        rows = cursor.fetchall()
        
        sites = []
        for row in rows:
            site = dict(row)
            # Map site_name to name for consistency with API
            if "site_name" in site:
                site["name"] = site.pop("site_name")
            sites.append(site)
        
        return sites
    finally:
        conn.close()


def main():
    """Main entry point for command-line usage."""
    parser = argparse.ArgumentParser(
        description="Query database directly for test validation"
    )
    parser.add_argument(
        "entity",
        choices=["job-search-sites"],
        help="Entity type to query"
    )
    parser.add_argument(
        "--environment",
        default="dev",
        choices=["dev", "test", "prod"],
        help="Environment name (default: dev)"
    )
    parser.add_argument(
        "--include-deleted",
        action="store_true",
        help="Include deleted records"
    )
    parser.add_argument(
        "--format",
        choices=["json", "pretty"],
        default="json",
        help="Output format (default: json)"
    )
    
    args = parser.parse_args()
    
    try:
        if args.entity == "job-search-sites":
            sites = query_job_search_sites(
                environment=args.environment,
                include_deleted=args.include_deleted
            )
            
            if args.format == "pretty":
                print(json.dumps(sites, indent=2))
            else:
                print(json.dumps(sites))
        else:
            print(f"Unknown entity: {args.entity}", file=sys.stderr)
            sys.exit(1)
    except Exception as e:
        print(json.dumps({"error": str(e)}), file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
