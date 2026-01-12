import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Application Detail Page
 * Uses data-qa selectors consistent with frontend/app/applications/[id]/page.tsx
 */
export class ApplicationDetailPage extends BasePage {
  // Selectors - use data-qa attributes where available
  readonly title: Locator;
  readonly backLink: Locator;
  readonly statusBadge: Locator;
  readonly editButton: Locator;
  readonly deleteButton: Locator;
  readonly addNoteButton: Locator;
  readonly notesList: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector (fallback to h1.h4 if data-qa not available)
    this.title = page.locator('h1.h4, h1.h3, h1.h2');
    // Back link
    this.backLink = page.locator('a:has-text("Back to Applications"), a:has-text("←")');
    // Status badge (fallback to .badge.bg-primary if data-qa not available)
    this.statusBadge = page.locator('.badge.bg-primary');
    // Buttons (use data-qa with application ID)
    this.editButton = page.locator('a:has-text("Edit"), button:has-text("Edit")');
    this.deleteButton = page.locator('button:has-text("Delete")');
    // Notes section
    this.addNoteButton = page.locator('button:has-text("Add Note")');
    this.notesList = page.locator('[data-qa*="notes-list"]');
  }

  /**
   * Navigate to application detail page
   * @param applicationId - Application ID
   */
  async navigate(applicationId: number): Promise<void> {
    await super.navigate(`/applications/${applicationId}`);
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await expect(this.title).toBeVisible();
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Get edit button for specific application
   * @param applicationId - Application ID
   */
  getEditButton(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-edit-button"]`);
  }

  /**
   * Get delete button for specific application
   * @param applicationId - Application ID
   */
  getDeleteButton(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-delete-button"]`);
  }

  /**
   * Click edit button
   * @param applicationId - Application ID
   */
  async clickEdit(applicationId: number): Promise<void> {
    const editButton = this.getEditButton(applicationId);
    await editButton.click();
  }

  /**
   * Click delete button
   * @param applicationId - Application ID
   */
  async clickDelete(applicationId: number): Promise<void> {
    const deleteButton = this.getDeleteButton(applicationId);
    await deleteButton.click();
  }

  /**
   * Click back link
   */
  async clickBack(): Promise<void> {
    await this.backLink.click();
  }

  /**
   * Get status text
   */
  async getStatus(): Promise<string | null> {
    return await this.statusBadge.textContent();
  }

  /**
   * Get add note button for specific application
   * @param applicationId - Application ID
   */
  getAddNoteButton(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-add-note-button"]`);
  }

  /**
   * Click add note button
   * @param applicationId - Application ID
   */
  async clickAddNote(applicationId: number): Promise<void> {
    const button = this.getAddNoteButton(applicationId);
    await button.click();
  }

  /**
   * Get note form textarea
   * @param applicationId - Application ID
   */
  getNoteFormTextarea(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-note-form-textarea"]`);
  }

  /**
   * Get note form submit button
   * @param applicationId - Application ID
   */
  getNoteFormSubmitButton(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-note-form-submit-button"]`);
  }

  /**
   * Add a note
   * @param applicationId - Application ID
   * @param noteText - Note text
   */
  async addNote(applicationId: number, noteText: string): Promise<void> {
    await this.clickAddNote(applicationId);
    const textarea = this.getNoteFormTextarea(applicationId);
    await textarea.fill(noteText);
    const submitButton = this.getNoteFormSubmitButton(applicationId);
    await submitButton.click();
  }

  /**
   * Get note by ID
   * @param applicationId - Application ID
   * @param noteId - Note ID
   */
  getNote(applicationId: number, noteId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-note-${noteId}"]`);
  }

  /**
   * Get note delete button
   * @param applicationId - Application ID
   * @param noteId - Note ID
   */
  getNoteDeleteButton(applicationId: number, noteId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-note-${noteId}-delete-button"]`);
  }

  /**
   * Delete a note
   * @param applicationId - Application ID
   * @param noteId - Note ID
   */
  async deleteNote(applicationId: number, noteId: number): Promise<void> {
    const deleteButton = this.getNoteDeleteButton(applicationId, noteId);
    await deleteButton.click();
  }
}
