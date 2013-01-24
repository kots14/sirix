package org.sirix.api;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.sirix.cache.LogKey;
import org.sirix.cache.RecordPageContainer;
import org.sirix.cache.TransactionLogPageCache;
import org.sirix.exception.SirixIOException;
import org.sirix.node.Kind;
import org.sirix.node.interfaces.Record;
import org.sirix.page.AttributeValuePage;
import org.sirix.page.NamePage;
import org.sirix.page.PageKind;
import org.sirix.page.PageReference;
import org.sirix.page.PathSummaryPage;
import org.sirix.page.RevisionRootPage;
import org.sirix.page.TextValuePage;
import org.sirix.page.UberPage;
import org.sirix.page.interfaces.KeyValuePage;
import org.sirix.page.interfaces.Page;

import com.google.common.base.Optional;

/**
 * Interface for reading pages.
 * 
 * @author Sebastian Graf, University of Konstanz
 * @author Johannes Lichtenberger, University of Konstanz
 */
public interface PageReadTrx extends AutoCloseable {

	/**
	 * Get {@link UberPage}.
	 * 
	 * @return the {@link UberPage} reference
	 */
	UberPage getUberPage();

	/**
	 * Get the session this transaction is bound to.
	 * 
	 * @return session instance
	 */
	Session getSession();

	/**
	 * Get a record from persistent storage.
	 * 
	 * @param key
	 *          the unique record-ID
	 * @param page
	 *          the page from which to fetch the record
	 * @return an {@link Optional} reference usually containing the node reference
	 * @throws SirixIOException
	 *           if an I/O error occured
	 */
	Optional<? extends Record> getRecord(final @Nonnegative long key,
			final @Nonnull PageKind page) throws SirixIOException;

	/**
	 * Current reference to actual revision-root page.
	 * 
	 * @return the current revision root page
	 * 
	 * @throws SirixIOException
	 *           if something odd happens within the creation process
	 */
	RevisionRootPage getActualRevisionRootPage() throws SirixIOException;

	/**
	 * Getting the name corresponding to the given key.
	 * 
	 * @param nameKey
	 *          name key for the term to search
	 * @param recordKind
	 *          kind of record
	 * @return the name
	 * @throws NullPointerException
	 *           if {@code kind} is {@code null}
	 */
	String getName(int nameKey, @Nonnull Kind recordKind);

	/**
	 * Get the number of references for a name.
	 * 
	 * @param nameKey
	 *          name key for the term to search
	 * @param recordKind
	 *          kind of record
	 * @return the number of references for a given keyy.
	 */
	int getNameCount(int nameKey, @Nonnull Kind recordKind);

	/**
	 * Getting the raw name related to the name key and the record kind.
	 * 
	 * @param nameKey
	 *          name key for the term to search
	 * @param recordKind
	 *          kind of record
	 * @return a byte array containing the raw name
	 * @throws NullPointerException
	 *           if {@code kind} is {@code null}
	 */
	byte[] getRawName(int nameKey, @Nonnull Kind recordKind);

	/**
	 * Close transaction.
	 * 
	 * @throws SirixIOException
	 *           if something weird happened in the storage
	 */
	@Override
	void close() throws SirixIOException;

	/**
	 * Get a the record page container with the full/modified pages from the page
	 * layer, given the unique page key and the page kind.
	 * 
	 * @param key
	 *          {@code key} of key/value page to get the record from
	 * @param pageKind
	 *          kind of page to lookup
	 * @return {@code the node} or {@code null} if it's not available
	 * @throws SirixIOException
	 *           if can't read recordPage
	 * @throws NullPointerException
	 *           if {@code key} is {@code null}
	 * @throws NullPointerException
	 *           if {@code pageKind} is {@code null}
	 * @throws IllegalArgumentException
	 *           if {@code key} is negative
	 */
	<K extends Comparable<? super K>, V extends Record, S extends KeyValuePage<K, V>> RecordPageContainer<S> getRecordPageContainer(
			@Nonnull @Nonnegative Long key, @Nonnull PageKind pageKind)
			throws SirixIOException;

	/** Determines if transaction is closed or not. */
	boolean isClosed();

	/**
	 * Get the revision number associated with the transaction.
	 * 
	 * @return the revision number
	 */
	int getRevisionNumber();

	/**
	 * Get page from cache.
	 * 
	 * @param reference
	 *          pointing to key of persistent storage
	 * @return page instance
	 * @throws SirixIOException
	 *           if an I/O error occurs
	 */
	Page getFromPageCache(@Nonnegative PageReference reference) throws SirixIOException;

	/**
	 * Clear the caches.
	 */
	void clearCaches();

	/**
	 * Put content from page cache into persistent storage.
	 * 
	 * @param pageLog
	 *          persistent page log
	 */
	void putPageCache(@Nonnull TransactionLogPageCache pageLog);

	/**
	 * Close the caches.
	 */
	void closeCaches();

	/**
	 * Calculate record page key from a given record key.
	 * 
	 * @param key
	 *          entry key to find record page key for
	 * @return record page key
	 * @throws IllegalArgumentException
	 *           if {code recordKey} < 0
	 */
	long pageKey(@Nonnegative long key);

	/**
	 * Get the {@link NamePage} associated with the current revision root.
	 * 
	 * @param revisionRoot
	 *          {@link RevisionRootPage} for which to get the {@link NamePage}
	 * @throws SirixIOException
	 *           if an I/O error occurs
	 */
	NamePage getNamePage(@Nonnull RevisionRootPage revisionRoot)
			throws SirixIOException;

	/**
	 * Get the {@link TextValuePage} associated with the current revision root.
	 * 
	 * @param revisionRoot
	 *          {@link RevisionRootPage} for which to get the {@link NamePage}
	 * @throws SirixIOException
	 *           if an I/O error occur@Nonnull RevisionRootPage revisionRoots
	 */
	TextValuePage getTextValuePage(@Nonnull RevisionRootPage revisionRoot)
			throws SirixIOException;

	/**
	 * Get the {@link AttributeValuePage} associated with the current revision
	 * root.
	 * 
	 * @param revisionRoot
	 *          {@link RevisionRootPage} for which to get the {@link NamePage}
	 * @throws SirixIOException
	 *           if an I/O error occurs
	 */
	AttributeValuePage getAttributeValuePage(
			@Nonnull RevisionRootPage revisionRoot) throws SirixIOException;

	/**
	 * Get the {@link PathSummaryPage} associated with the current revision root.
	 * 
	 * @param revisionRoot
	 *          {@link RevisionRootPage} for which to get the {@link NamePage}
	 * @throws SirixIOException
	 *           if an I/O error occurs
	 */
	PathSummaryPage getPathSummaryPage(@Nonnull RevisionRootPage revisionRoot)
			throws SirixIOException;

	/**
	 * Get the page reference pointing to the page denoted by {@code pageKey}.
	 * 
	 * @param startReference
	 *          the start reference (for instance to the indirect tree or the
	 *          root-node of a BPlusTree)
	 * @param pageKey
	 *          the unique key of the page to search for
	 * @param pageKind
	 *          the kind of subtree
	 * @return {@link PageReference} instance pointing to the page denoted by
	 *         {@code key}
	 * @throws SirixIOException
	 *           if an I/O error occurs
	 * @throws IllegalArgumentException
	 *           if {code pageKey} < 0
	 */
	PageReference getPageReferenceForPage(@Nonnull PageReference startReference,
			@Nonnegative long pageKey, @Nonnull PageKind pageKind)
			throws SirixIOException;
}
