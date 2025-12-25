"""
Custom error classes for ONE GOAL API
Matches API contract error format
"""
from fastapi import HTTPException, status
from typing import Optional, Dict, Any


class NotFoundError(HTTPException):
    """404 Not Found error."""
    
    def __init__(self, resource: str, resource_id: int):
        super().__init__(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={
                "error": f"{resource} not found",
                "code": 404,
                "details": {"id": resource_id}
            }
        )


class ValidationError(HTTPException):
    """400 Bad Request - Validation error."""
    
    def __init__(self, message: str, field: Optional[str] = None):
        detail = {
            "error": "Validation error",
            "code": 400,
            "details": {"message": message}
        }
        if field:
            detail["details"]["field"] = field
        
        super().__init__(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=detail
        )


class ConflictError(HTTPException):
    """409 Conflict - Unique constraint violation."""
    
    def __init__(self, message: str, details: Optional[Dict[str, Any]] = None):
        detail = {
            "error": "Conflict",
            "code": 409,
            "details": {"message": message}
        }
        if details:
            detail["details"].update(details)
        
        super().__init__(
            status_code=status.HTTP_409_CONFLICT,
            detail=detail
        )


class ForeignKeyError(HTTPException):
    """422 Unprocessable Entity - Foreign Key violation."""
    
    def __init__(self, message: str, field: Optional[str] = None):
        detail = {
            "error": "Foreign Key constraint failed",
            "code": 422,
            "details": {"message": message}
        }
        if field:
            detail["details"]["field"] = field
        
        super().__init__(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=detail
        )


class InternalServerError(HTTPException):
    """500 Internal Server Error."""
    
    def __init__(self, message: str = "Internal server error"):
        super().__init__(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={
                "error": message,
                "code": 500
            }
        )
