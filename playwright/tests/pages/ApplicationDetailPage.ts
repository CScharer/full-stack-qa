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
    // Title selector - uses data-qa with application ID (use getTitle(applicationId) method)
    // Note: Title requires application ID, so use getTitle() method instead of this property
    this.title = page.locator('[data-qa^="application-detail-"][data-qa$="-title"]');
    // Back link - uses data-qa attribute
    this.backLink = page.locator('[data-qa="application-detail-back-link"]');
    // Status badge - uses data-qa with application ID (use getStatusBadge(applicationId) method)
    // Note: Status badge requires application ID, so use getStatusBadge() method instead of this property
    this.statusBadge = page.locator('[data-qa^="application-detail-"][data-qa$="-status-badge"]');
    // Buttons - methods already use data-qa with application ID, these are fallbacks
    // Edit button fallback (methods use getEditButton which has data-qa)
    this.editButton = page.locator('[data-qa^="application-detail-"][data-qa$="-edit-button"]');
    // Delete button fallback (methods use getDeleteButton which has data-qa)
    this.deleteButton = page.locator('[data-qa^="application-detail-"][data-qa$="-delete-button"]');
    // Notes section
    // Add note button fallback (methods use getAddNoteButton which has data-qa)
    this.addNoteButton = page.locator('[data-qa^="application-detail-"][data-qa$="-add-note-button"]');
    this.notesList = page.locator('[data-qa*="notes-list"]');
  }

  /**
   * Navigate to application detail page
   * @param applicationId - Application ID
   */
  async navigateToApplication(applicationId: number): Promise<void> {
    await super.navigate(`/applications/${applicationId}`);
  }

  /**
   * Get title locator for specific application
   * @param applicationId - Application ID
   */
  getTitleLocator(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-title"]`);
  }

  /**
   * Get status badge for specific application
   * @param applicationId - Application ID
   */
  getStatusBadge(applicationId: number): Locator {
    return this.page.locator(`[data-qa="application-detail-${applicationId}-status-badge"]`);
  }

  /**
   * Verify page has loaded
   * @param applicationId - Application ID (optional, for title verification)
   */
  async verifyPageLoaded(applicationId?: number): Promise<void> {
    await this.waitForPageLoad();
    if (applicationId) {
      await expect(this.getTitleLocator(applicationId)).toBeVisible();
    } else {
      // Fallback: just check that body is visible
      await expect(this.page.locator('body')).toBeVisible();
    }
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
   * @param applicationId - Application ID
   */
  async getStatus(applicationId: number): Promise<string | null> {
    const statusBadge = this.getStatusBadge(applicationId);
    return await statusBadge.textContent();
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
