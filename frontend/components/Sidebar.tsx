'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState, useEffect } from 'react';
import { Button } from './ui/Button';

interface NavItem {
  href: string;
  label: string;
  description: string;
  icon?: string;
}

const navItems: NavItem[] = [
  {
    href: '/',
    label: 'Home',
    description: 'Dashboard and applications',
  },
  {
    href: '/applications',
    label: 'Applications',
    description: 'Manage your job applications',
  },
  {
    href: '/companies',
    label: 'Companies',
    description: 'Track companies you\'ve applied to',
  },
  {
    href: '/contacts',
    label: 'Contacts',
    description: 'Manage recruiters and contacts',
  },
  {
    href: '/clients',
    label: 'Clients',
    description: 'Manage client relationships',
  },
  {
    href: '/notes',
    label: 'Notes',
    description: 'Track notes for applications',
  },
  {
    href: '/job-search-sites',
    label: 'Job Search Sites',
    description: 'Manage job search platforms',
  },
];

export function Sidebar() {
  const pathname = usePathname();
  const [isOpen, setIsOpen] = useState(false);

  // Close sidebar on mobile when route changes
  useEffect(() => {
    setIsOpen(false);
  }, [pathname]);

  return (
    <>
      {/* Mobile toggle button */}
      <div className="d-md-none position-fixed top-0 start-0 m-3 z-4" style={{ zIndex: 1040 }} data-qa="sidebar-mobile-toggle">
        <Button
          variant="primary"
          size="sm"
          onClick={() => setIsOpen(!isOpen)}
          data-qa="sidebar-toggle-button"
        >
          ☰ Menu
        </Button>
      </div>

      {/* Overlay for mobile */}
      {isOpen && (
        <div
          className="d-md-none position-fixed top-0 start-0 w-100 h-100 bg-dark bg-opacity-50"
          style={{ zIndex: 1030 }}
          onClick={() => setIsOpen(false)}
          data-qa="sidebar-overlay"
        />
      )}

      {/* Sidebar */}
      <aside
        className={`bg-light border-end shadow-sm ${
          isOpen ? 'd-block position-fixed' : 'd-none d-md-block position-relative'
        }`}
        style={{ 
          width: '280px', 
          height: isOpen ? '100vh' : 'auto',
          minHeight: '100vh',
          overflowY: 'auto', 
          zIndex: isOpen ? 1035 : 'auto',
          flexShrink: 0
        }}
        data-qa="sidebar"
      >
        <div className="p-3 p-md-4">
          <div className="d-flex justify-content-between align-items-center mb-3 mb-md-4">
            <Link href="/" className="text-decoration-none">
              <h2 className="h5 mb-0 text-primary fw-bold" data-qa="sidebar-title">
                Navigation
              </h2>
            </Link>
            <Button
              variant="link"
              size="sm"
              className="d-md-none text-dark"
              onClick={() => setIsOpen(false)}
              data-qa="sidebar-close-button"
            >
              ✕
            </Button>
          </div>

          <nav data-qa="sidebar-navigation">
            <ul className="list-unstyled mb-0">
              {navItems.map((item) => {
                const isActive = pathname === item.href || (item.href !== '/' && pathname?.startsWith(item.href));
                return (
                  <li key={item.href} className="mb-2">
                    <Link
                      href={item.href}
                      className={`text-decoration-none d-block p-2 rounded ${
                        isActive
                          ? 'bg-primary text-white'
                          : 'text-dark hover-bg-light'
                      }`}
                      onClick={() => setIsOpen(false)}
                      data-qa={`sidebar-nav-${item.label.toLowerCase().replace(/\s+/g, '-')}`}
                    >
                      <div className="fw-medium">{item.label}</div>
                      <small className={`${isActive ? 'text-white-50' : 'text-muted'}`}>
                        {item.description}
                      </small>
                    </Link>
                  </li>
                );
              })}
            </ul>
          </nav>
        </div>
      </aside>

    </>
  );
}
