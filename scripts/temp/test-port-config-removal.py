#!/usr/bin/env python3
# scripts/temp/test-port-config-removal.py
# Test Port Configuration After ports.json Removal
#
# Purpose: Verify that port_config.py works correctly after removing ports.json dependency
#
# Usage:
#   python3 scripts/temp/test-port-config-removal.py
#
# Description:
#   Tests all functions in config/port_config.py to ensure they work correctly
#   after the removal of ports.json fallback. Verifies that:
#   - get_environment_config() works for all environments
#   - get_backend_url() and get_frontend_url() return correct URLs
#   - get_api_config() and get_api_base_path() work correctly
#   - get_ports_for_environment() returns correct port configurations
#
# Dependencies:
#   - Python 3.x
#   - config/port_config.py module
#   - config/environments.json file
#
# Output:
#   - Console output showing test results
#   - Exits with 0 on success, non-zero on failure
#
# Notes:
#   - This is a temporary test script for verifying ports.json removal
#   - Should be deleted after verification is complete
#
# Last Updated: January 2026

"""Test port_config.py after ports.json removal"""
import sys
from pathlib import Path

# Add project root to path
project_root = Path(__file__).parent.parent.parent
sys.path.insert(0, str(project_root))

from config.port_config import (
    get_environment_config,
    get_backend_url,
    get_frontend_url,
    get_api_config,
    get_api_base_path,
    get_ports_for_environment
)

def main():
    """Run all port configuration tests"""
    print("Testing port_config.py after ports.json removal...")
    print()

    # Test 1: Get environment config
    try:
        dev_config = get_environment_config('dev')
        print(f"✅ get_environment_config('dev'):")
        print(f"   Frontend port: {dev_config['frontend']['port']}")
        print(f"   Backend port: {dev_config['backend']['port']}")
        print(f"   Frontend URL: {dev_config['frontend']['url']}")
        print(f"   Backend URL: {dev_config['backend']['url']}")
    except Exception as e:
        print(f"❌ get_environment_config('dev') failed: {e}")
        return 1

    # Test 2: Get URLs
    try:
        backend_url = get_backend_url('dev')
        frontend_url = get_frontend_url('test')
        print(f"✅ get_backend_url('dev'): {backend_url}")
        print(f"✅ get_frontend_url('test'): {frontend_url}")
    except Exception as e:
        print(f"❌ URL functions failed: {e}")
        return 1

    # Test 3: Get API config
    try:
        api_config = get_api_config()
        api_base_path = get_api_base_path()
        print(f"✅ get_api_config(): basePath = {api_config.get('basePath')}")
        print(f"✅ get_api_base_path(): {api_base_path}")
    except Exception as e:
        print(f"❌ API config functions failed: {e}")
        return 1

    # Test 4: Get ports for environment
    try:
        ports = get_ports_for_environment('prod')
        print(f"✅ get_ports_for_environment('prod'):")
        print(f"   Frontend: {ports['frontend']}")
        print(f"   Backend: {ports['backend']}")
    except Exception as e:
        print(f"❌ get_ports_for_environment failed: {e}")
        return 1

    # Test 5: Test all environments
    try:
        for env in ['dev', 'test', 'prod']:
            config = get_environment_config(env)
            print(f"✅ Environment '{env}': frontend={config['frontend']['port']}, backend={config['backend']['port']}")
    except Exception as e:
        print(f"❌ Environment iteration failed: {e}")
        return 1

    print()
    print("✅ All port_config.py tests passed!")
    return 0

if __name__ == '__main__':
    sys.exit(main())
