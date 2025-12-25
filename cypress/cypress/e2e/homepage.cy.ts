/// <reference types="cypress" />

describe('HomePage', () => {
  beforeEach(() => {
    // Maximize browser window
    cy.viewport(1920, 1080);
    // Visit the home page
    cy.visit('/');
    // Wait for page to load
    cy.get('body').should('be.visible');
  });

  it('should load the home page', () => {
    // Verify the page title in the browser tab
    cy.title().should('contain', 'Job Search Application');
    
    // Verify the page has loaded by checking for main content
    cy.get('body').should('be.visible');
  });

  it('should display the navigation panel', () => {
    // Verify sidebar is visible
    cy.get('[data-qa="sidebar"]').should('be.visible');
    
    // Verify navigation title is visible and contains "Navigation"
    cy.get('[data-qa="sidebar-title"]').contains('Navigation').should('be.visible');
    
    // Verify navigation elements are present
    cy.get('[data-qa="sidebar-navigation"]').should('be.visible');
    cy.get('[data-qa="sidebar-nav-home"]').should('be.visible');
  });
});
