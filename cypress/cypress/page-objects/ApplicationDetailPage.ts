/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Application Detail Page
 * Uses data-qa selectors consistent with frontend/app/applications/[id]/page.tsx
 */
export class ApplicationDetailPage extends BasePage {
  // Selectors - use data-qa attributes where available
  // Note: Most selectors require application ID, so use getter methods instead of these properties
  readonly backLinkSelector = '[data-qa="application-detail-back-link"]';
  readonly notesListSelector = '[data-qa*="notes-list"]';

  /**
   * Navigate to application detail page
   * @param applicationId - Application ID
   */
  navigateToApplication(applicationId: number): void {
    this.visit(`/applications/${applicationId}`);
  }

  /**
   * Get title locator for specific application
   * @param applicationId - Application ID
   */
  getTitleLocator(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-title"]`);
  }

  /**
   * Get status badge for specific application
   * @param applicationId - Application ID
   */
  getStatusBadge(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-status-badge"]`);
  }

  /**
   * Verify page has loaded
   * @param applicationId - Application ID (optional, for title verification)
   */
  verifyPageLoaded(applicationId?: number): void {
    this.waitForPageLoad();
    if (applicationId) {
      this.getTitleLocator(applicationId).should('be.visible');
    }
    cy.get('body').should('be.visible');
  }

  /**
   * Get edit button for specific application
   * @param applicationId - Application ID
   */
  getEditButton(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-edit-button"]`);
  }

  /**
   * Get delete button for specific application
   * @param applicationId - Application ID
   */
  getDeleteButton(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-delete-button"]`);
  }

  /**
   * Click edit button
   * @param applicationId - Application ID
   */
  clickEdit(applicationId: number): void {
    this.getEditButton(applicationId).click();
  }

  /**
   * Click delete button
   * @param applicationId - Application ID
   */
  clickDelete(applicationId: number): void {
    this.getDeleteButton(applicationId).click();
  }

  /**
   * Click back link
   */
  clickBack(): void {
    cy.get(this.backLinkSelector).click();
  }

  /**
   * Get status text
   * @param applicationId - Application ID
   */
  getStatus(applicationId: number): Cypress.Chainable<string> {
    return this.getStatusBadge(applicationId).invoke('text');
  }

  /**
   * Get add note button for specific application
   * @param applicationId - Application ID
   */
  getAddNoteButton(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-add-note-button"]`);
  }

  /**
   * Click add note button
   * @param applicationId - Application ID
   */
  clickAddNote(applicationId: number): void {
    this.getAddNoteButton(applicationId).click();
  }

  /**
   * Get note form textarea
   * @param applicationId - Application ID
   */
  getNoteFormTextarea(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-note-form-textarea"]`);
  }

  /**
   * Get note form submit button
   * @param applicationId - Application ID
   */
  getNoteFormSubmitButton(applicationId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-note-form-submit-button"]`);
  }

  /**
   * Add a note
   * @param applicationId - Application ID
   * @param noteText - Note text
   */
  addNote(applicationId: number, noteText: string): void {
    this.clickAddNote(applicationId);
    this.getNoteFormTextarea(applicationId).type(noteText);
    this.getNoteFormSubmitButton(applicationId).click();
  }

  /**
   * Get note by ID
   * @param applicationId - Application ID
   * @param noteId - Note ID
   */
  getNote(applicationId: number, noteId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-note-${noteId}"]`);
  }

  /**
   * Get note delete button
   * @param applicationId - Application ID
   * @param noteId - Note ID
   */
  getNoteDeleteButton(applicationId: number, noteId: number): Cypress.Chainable {
    return cy.get(`[data-qa="application-detail-${applicationId}-note-${noteId}-delete-button"]`);
  }

  /**
   * Delete a note
   * @param applicationId - Application ID
   * @param noteId - Note ID
   */
  deleteNote(applicationId: number, noteId: number): void {
    this.getNoteDeleteButton(applicationId, noteId).click();
  }
}
