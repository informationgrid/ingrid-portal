package de.ingrid.portal.search;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author joachim
 * TODO: NEED TO IMPLEMENT SERIALIZATION INTERFACE
 *
 */
public class SearchResultPageState implements Serializable {

	/**
	 * TODO: Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2845917294938815949L;

	/**
	 * TODO: Comment for <code>query</code>
	 */
	private String query = null;

	/**
	 * TODO: Comment for <code>isActionProcessed</code>
	 */
	private boolean isActionProcessed = false;

	/**
	 * TODO: Comment for <code>isSimilarOpen</code>
	 */
	private boolean isSimilarOpen = false;

	/**
	 * TODO: Comment for <code>selectedDS</code>
	 */
	private String selectedDS = null;

	/**
	 * TODO: Comment for <code>similarRoot</code>
	 */
	private SimilarTreeNode similarRoot = null;

	/**
	 * TODO: Comment for <code>rankedNavStart</code>
	 */
	private int rankedNavStart = 0;

	/**
	 * TODO: Comment for <code>rankedNavLimit</code>
	 */
	private int rankedNavLimit = 10;

	
	/**
	 * TODO: Comment for <code>rankedPage</code>
	 */
	private int rankedCurrentPage = 1;

	/**
	 * TODO: Comment for <code>rankedNumberOfPages</code>
	 */
	private int rankedNumberOfPages = 1;

	/**
	 * TODO: Comment for <code>unrankedNavStart</code>
	 */
	private int unrankedNavStart = 0;

	/**
	 * TODO: Comment for <code>unrankedNavLimit</code>
	 */
	private int unrankedNavLimit = 10;
	
	/**
	 * TODO: Comment for <code>unrankedCurrentPage</code>
	 */
	private int unrankedCurrentPage = 1;
	
	/**
	 * TODO: Comment for <code>unrankedNumberOfPages</code>
	 */
	private int unrankedNumberOfPages = 1;

	/**
	 * TODO: Comment for <code>rankedResultList</code>
	 */
	private SearchResultList rankedResultList = null;

	/**
	 * TODO: Comment for <code>unrankedResultList</code>
	 */
	private SearchResultList unrankedResultList = null;

	
	/**
	 * @return Returns the isActionProcessed.
	 */
	public boolean isActionProcessed() {
		return isActionProcessed;
	}

	/**
	 * @param isActionProcessed
	 *            The isActionProcessed to set.
	 */
	public void setActionProcessed(boolean isActionProcessed) {
		this.isActionProcessed = isActionProcessed;
	}

	/**
	 * @return Returns the isSimilarOpen.
	 */
	public boolean isSimilarOpen() {
		return isSimilarOpen;
	}

	/**
	 * @param isSimilarOpen
	 *            The isSimilarOpen to set.
	 */
	public void setSimilarOpen(boolean isSimilarOpen) {
		this.isSimilarOpen = isSimilarOpen;
	}

	/**
	 * @return Returns the query.
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            The query to set.
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * @return Returns the rankedNavLimit.
	 */
	public int getRankedNavLimit() {
		return rankedNavLimit;
	}

	/**
	 * @param rankedNavLimit
	 *            The rankedNavLimit to set.
	 */
	public void setRankedNavLimit(int rankedNavLimit) {
		this.rankedNavLimit = rankedNavLimit;
	}

	/**
	 * @return Returns the rankedNavStart.
	 */
	public int getRankedNavStart() {
		return rankedNavStart;
	}

	/**
	 * @param rankedNavStart
	 *            The rankedNavStart to set.
	 */
	public void setRankedNavStart(int rankedNavStart) {
		this.rankedNavStart = rankedNavStart;
	}

	/**
	 * @return Returns the rankedResultList.
	 */
	public SearchResultList getRankedResultList() {
		return rankedResultList;
	}

	/**
	 * @param rankedResultList
	 *            The rankedResultList to set.
	 */
	public void setRankedResultList(SearchResultList rankedResultList) {
		this.rankedResultList = rankedResultList;
	}

	/**
	 * @return Returns the selectedDS.
	 */
	public String getSelectedDS() {
		return selectedDS;
	}

	/**
	 * @param selectedDS
	 *            The selectedDS to set.
	 */
	public void setSelectedDS(String selectedDS) {
		this.selectedDS = selectedDS;
	}

	/**
	 * @return Returns the similarRoot.
	 */
	public SimilarTreeNode getSimilarRoot() {
		return similarRoot;
	}

	/**
	 * @param similarRoot
	 *            The similarRoot to set.
	 */
	public void setSimilarRoot(SimilarTreeNode similarRoot) {
		this.similarRoot = similarRoot;
	}

	/**
	 * @return Returns the unrankedNavLimit.
	 */
	public int getUnrankedNavLimit() {
		return unrankedNavLimit;
	}

	/**
	 * @param unrankedNavLimit
	 *            The unrankedNavLimit to set.
	 */
	public void setUnrankedNavLimit(int unrankedNavLimit) {
		this.unrankedNavLimit = unrankedNavLimit;
	}

	/**
	 * @return Returns the unrankedNavStart.
	 */
	public int getUnrankedNavStart() {
		return unrankedNavStart;
	}

	/**
	 * @param unrankedNavStart
	 *            The unrankedNavStart to set.
	 */
	public void setUnrankedNavStart(int unrankedNavStart) {
		this.unrankedNavStart = unrankedNavStart;
	}

	/**
	 * @return Returns the unrankedResultList.
	 */
	public SearchResultList getUnrankedResultList() {
		return unrankedResultList;
	}

	/**
	 * @param unrankedResultList
	 *            The unrankedResultList to set.
	 */
	public void setUnrankedResultList(SearchResultList unrankedResultList) {
		this.unrankedResultList = unrankedResultList;
	}

	/**
	 * @return Returns the rankedCurrentPage.
	 */
	public int getRankedCurrentPage() {
		return rankedCurrentPage;
	}

	/**
	 * @param rankedCurrentPage The rankedCurrentPage to set.
	 */
	public void setRankedCurrentPage(int rankedCurrentPage) {
		this.rankedCurrentPage = rankedCurrentPage;
	}

	/**
	 * @return Returns the rankedNumberOfPages.
	 */
	public int getRankedNumberOfPages() {
		return rankedNumberOfPages;
	}

	/**
	 * @param rankedNumberOfPages The rankedNumberOfPages to set.
	 */
	public void setRankedNumberOfPages(int rankedNumberOfPages) {
		this.rankedNumberOfPages = rankedNumberOfPages;
	}

	/**
	 * @return Returns the unrankedCurrentPage.
	 */
	public int getUnrankedCurrentPage() {
		return unrankedCurrentPage;
	}

	/**
	 * @param unrankedCurrentPage The unrankedCurrentPage to set.
	 */
	public void setUnrankedCurrentPage(int unrankedCurrentPage) {
		this.unrankedCurrentPage = unrankedCurrentPage;
	}

	/**
	 * @return Returns the unrankedNumberOfPages.
	 */
	public int getUnrankedNumberOfPages() {
		return unrankedNumberOfPages;
	}

	/**
	 * @param unrankedNumberOfPages The unrankedNumberOfPages to set.
	 */
	public void setUnrankedNumberOfPages(int unrankedNumberOfPages) {
		this.unrankedNumberOfPages = unrankedNumberOfPages;
	}

	private void writeObject(ObjectOutputStream out) throws IOException	{
		out.writeObject(this.query);
		out.writeObject(this.rankedResultList);
		out.writeObject(this.unrankedResultList);
		out.writeObject(this.selectedDS);
		out.writeObject(this.similarRoot);
		out.writeBoolean(this.isActionProcessed);
		out.writeBoolean(this.isSimilarOpen);
		out.writeInt(this.rankedCurrentPage);
		out.writeInt(this.rankedNavLimit);
		out.writeInt(this.rankedNavStart);
		out.writeInt(this.rankedNumberOfPages);
		out.writeInt(this.unrankedCurrentPage);
		out.writeInt(this.unrankedNavLimit);
		out.writeInt(this.unrankedNavStart);
		out.writeInt(this.unrankedNumberOfPages);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.query = (String)in.readObject();
		this.rankedResultList = (SearchResultList)in.readObject();
		this.unrankedResultList = (SearchResultList)in.readObject();
		this.selectedDS = (String)in.readObject();
		this.similarRoot = (SimilarTreeNode)in.readObject();
		this.isActionProcessed = in.readBoolean();
		this.isSimilarOpen = in.readBoolean();
		this.rankedCurrentPage = in.readInt();
		this.rankedNavLimit = in.readInt();
		this.rankedNavStart = in.readInt();
		this.rankedNumberOfPages = in.readInt();
		this.unrankedCurrentPage = in.readInt();
		this.unrankedNavLimit = in.readInt();
		this.unrankedNavStart = in.readInt();
		this.unrankedNumberOfPages = in.readInt();
	}	
	
}
