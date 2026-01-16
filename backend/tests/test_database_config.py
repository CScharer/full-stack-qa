"""
Test script to verify database configuration and path resolution.

This script tests the priority-based database path resolution:
1. DATABASE_PATH env var (full path)
2. DATABASE_NAME env var + DATABASE_DIR
3. ENVIRONMENT env var → full_stack_qa_{env}.db
4. Default → full_stack_qa_dev.db

Run this script to verify the configuration works correctly.
"""
import os
import sys
from pathlib import Path

# Add parent directory to path to import app modules
sys.path.insert(0, str(Path(__file__).parent.parent))

from app.config import get_database_path, _validate_not_schema_database


def test_default_database():
    """Test default database path (should be full_stack_qa_dev.db)"""
    print("\n=== Test 1: Default Database Path ===")
    # Clear any environment variables
    for key in ["DATABASE_PATH", "DATABASE_NAME", "DATABASE_DIR", "ENVIRONMENT"]:
        if key in os.environ:
            del os.environ[key]
    
    db_path = get_database_path()
    print(f"Default database path: {db_path}")
    assert db_path.name == "full_stack_qa_dev.db", f"Expected full_stack_qa_dev.db, got {db_path.name}"
    print("✅ PASS: Default database is full_stack_qa_dev.db")


def test_environment_based_selection():
    """Test environment-based database selection"""
    print("\n=== Test 2: Environment-Based Selection ===")
    
    # Test dev environment
    os.environ["ENVIRONMENT"] = "dev"
    if "DATABASE_PATH" in os.environ:
        del os.environ["DATABASE_PATH"]
    if "DATABASE_NAME" in os.environ:
        del os.environ["DATABASE_NAME"]
    
    db_path = get_database_path()
    print(f"ENVIRONMENT=dev → {db_path.name}")
    assert db_path.name == "full_stack_qa_dev.db", f"Expected full_stack_qa_dev.db, got {db_path.name}"
    print("✅ PASS: ENVIRONMENT=dev selects full_stack_qa_dev.db")
    
    # Test test environment
    os.environ["ENVIRONMENT"] = "test"
    db_path = get_database_path()
    print(f"ENVIRONMENT=test → {db_path.name}")
    assert db_path.name == "full_stack_qa_test.db", f"Expected full_stack_qa_test.db, got {db_path.name}"
    print("✅ PASS: ENVIRONMENT=test selects full_stack_qa_test.db")
    
    # Test prod environment
    os.environ["ENVIRONMENT"] = "prod"
    db_path = get_database_path()
    print(f"ENVIRONMENT=prod → {db_path.name}")
    assert db_path.name == "full_stack_qa_prod.db", f"Expected full_stack_qa_prod.db, got {db_path.name}"
    print("✅ PASS: ENVIRONMENT=prod selects full_stack_qa_prod.db")
    
    del os.environ["ENVIRONMENT"]


def test_database_name_env_var():
    """Test DATABASE_NAME environment variable"""
    print("\n=== Test 3: DATABASE_NAME Environment Variable ===")
    
    os.environ["DATABASE_NAME"] = "custom_test.db"
    if "DATABASE_PATH" in os.environ:
        del os.environ["DATABASE_PATH"]
    if "ENVIRONMENT" in os.environ:
        del os.environ["ENVIRONMENT"]
    
    db_path = get_database_path()
    print(f"DATABASE_NAME=custom_test.db → {db_path.name}")
    assert db_path.name == "custom_test.db", f"Expected custom_test.db, got {db_path.name}"
    print("✅ PASS: DATABASE_NAME env var works correctly")
    
    del os.environ["DATABASE_NAME"]


def test_database_path_env_var():
    """Test DATABASE_PATH environment variable (highest priority)"""
    print("\n=== Test 4: DATABASE_PATH Environment Variable (Priority 1) ===")
    
    # Create a temporary path
    temp_path = Path("/tmp/test_custom_path.db")
    os.environ["DATABASE_PATH"] = str(temp_path)
    
    db_path = get_database_path()
    print(f"DATABASE_PATH={temp_path} → {db_path}")
    assert db_path == temp_path.resolve(), f"Expected {temp_path.resolve()}, got {db_path}"
    print("✅ PASS: DATABASE_PATH env var has highest priority")
    
    del os.environ["DATABASE_PATH"]


def test_schema_database_validation():
    """Test that schema database cannot be used for runtime"""
    print("\n=== Test 5: Schema Database Validation ===")
    
    # Try to use schema database - should raise ValueError
    schema_path = Path(__file__).parent.parent.parent / "data" / "Core" / "full_stack_qa.db"
    os.environ["DATABASE_PATH"] = str(schema_path)
    
    try:
        db_path = get_database_path()
        print(f"❌ FAIL: Should have raised ValueError for schema database")
        assert False, "Should have raised ValueError"
    except ValueError as e:
        print(f"✅ PASS: Correctly rejected schema database: {e}")
    
    del os.environ["DATABASE_PATH"]


def test_validation_function():
    """Test the validation function directly"""
    print("\n=== Test 6: Validation Function ===")
    
    # Test with schema database name
    schema_path = Path("/some/path/full_stack_qa.db")
    try:
        _validate_not_schema_database(schema_path)
        print("❌ FAIL: Should have raised ValueError")
        assert False
    except ValueError:
        print("✅ PASS: Validation correctly rejects schema database")
    
    # Test with environment database (should pass)
    dev_path = Path("/some/path/full_stack_qa_dev.db")
    try:
        _validate_not_schema_database(dev_path)
        print("✅ PASS: Validation allows environment databases")
    except ValueError:
        print("❌ FAIL: Should allow environment databases")
        assert False


def main():
    """Run all tests"""
    print("=" * 60)
    print("Database Configuration Tests")
    print("=" * 60)
    
    try:
        test_default_database()
        test_environment_based_selection()
        test_database_name_env_var()
        test_database_path_env_var()
        test_schema_database_validation()
        test_validation_function()
        
        print("\n" + "=" * 60)
        print("✅ ALL TESTS PASSED")
        print("=" * 60)
        return 0
    except AssertionError as e:
        print(f"\n❌ TEST FAILED: {e}")
        return 1
    except Exception as e:
        print(f"\n❌ ERROR: {e}")
        import traceback
        traceback.print_exc()
        return 1


if __name__ == "__main__":
    sys.exit(main())

