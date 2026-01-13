/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Notes List Page
 * Uses data-qa selectors consistent with frontend/app/notes/page.tsx
 */
export class NotesPage extends BasePage {
  // Selectors - use data-qa attributes where available
  private readonly title = '[data-qa="notes-title"]';
  private readonly newNoteButton = 'a[href="/notes/new"], button:has-text("Add")';
  private readonly filtersCard = '[data-qa="notes-filters"]';
  private readonly applicationIdFilter = '[data-qa="notes-filter-application-id"]';
  private readonly notesList = '[data-qa="notes-list-card"]';
  private readonly emptyState = '[data-qa="notes-empty-state"]';
  private readonly paginationPreviousButton = '[data-qa="notes-pagination-previous-button"]';
  private readonly paginationNextButton = '[data-qa="notes-pagination-next-button"]';

  /**
   * Navigate to notes page
   */
  navigate(): void {
    this.visit('/notes');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    cy.get(this.title).should('contain', 'Notes');
    cy.get('body').should('be.visible');
  }

  /**
   * Click New Note button
   * NOTE: This method will not work on the notes page as there is no "New Note" button.
   * Notes can only be created from within an application page.
   * This method is kept for backward compatibility.
   */
  clickNewNote(): void {
    // This will fail if called - notes page doesn't have a new note button
    cy.get(this.newNoteButton).click();
  }

  /**
   * Filter by application ID
   * @param applicationId - Application ID to filter by
   */
  filterByApplicationId(applicationId: number): void {
    cy.get(this.applicationIdFilter).clear().type(applicationId.toString());
  }

  /**
   * Verify notes list is visible
   */
  verifyListVisible(): void {
    cy.get(this.notesList).first().should('be.visible');
  }

  /**
   * Verify empty state is visible
   */
  verifyEmptyState(): void {
    cy.get(this.emptyState).should('be.visible');
  }
}
