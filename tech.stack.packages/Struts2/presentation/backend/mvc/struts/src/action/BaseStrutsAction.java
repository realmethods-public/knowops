#header()
package ${aib.getRootPackageName()}.#getActionPackageName();

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class BaseStrutsAction
    extends FrameworkBaseStrutsAction
{
    public String [] getSelections()
    {
        return selections;
    }

    public String noOp()
    {
        return SUCCESS;
    }

    public void setSelections(String [] data)
    {
        selections = data;
    }

    public String getSelection()
    {
        return selection;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void setSelection(String data)
    {
        selection = data;
    }

    public void setCacheOnly(String is)
    {
        cacheOnly = is;
    }

    public boolean cacheOnly()
    {
        return ((cacheOnly != null) && (cacheOnly.lastIndexOf("true") >= 0));
    }

    /**
     * Called by Struts so the Action can prepare itself.
     * Implemented from FrameworkBaseStrutsAction
     * Overload to something more specific, but be sure to call this class as well
     */
    public void prepare() throws Exception
    {
        onPrepare();

        // might want to do something interesting here...	    
    }

    /**
     * Helper method to preparations
     */
    protected void onPrepare()
    {
        selections = null;
        selection  = null;
        loaded     = false;
    }

    /**
     * Helper method to retrieve a Collection of keys from the selections
     *
     * @return                Collection<String>
     */
    protected Collection<String> getSelectionsAsString()
    {
        Collection<String> collection = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(selections[ 0 ], ",");

        while (tokenizer.hasMoreTokens())
        {
            collection.add(tokenizer.nextToken());
        }

        return (collection);
    }

    protected void cacheAsFocusObject(Object obj)
    {
        // cache it to the session
        getServletRequest().getSession().setAttribute("focusObject", obj);
    }

    protected Object getFocusObjectFromCache()
    {
        return (getServletRequest().getSession().getAttribute("focusObject"));
    }

    protected void cacheAsMostRecentlyCreatedObject(Object obj)
    {
        // cache it to the session
        getServletRequest().getSession().setAttribute("mostRecentlyCreated", obj);
    }

    protected Object getMostRecentlyCreatedObject()
    {
        return (getServletRequest().getSession().getAttribute("mostRecentlyCreated"));
    }

/*
    public ApplicationUSOM getAppNameSpace()
    {
        return appUSOM;
    }
*/
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return how many rows we want to have into the grid
     */
    public Integer getRows()
    {
        return rows;
    }

    /**
     * @param rows
     *          how many rows we want to have into the grid
     */
    public void setRows(Integer rows)
    {
        this.rows = rows;
    }

    /**
     * @return current page of the query
     */
    public Integer getPage()
    {
        return page;
    }

    /**
     * @param page
     *          current page of the query
     */
    public void setPage(Integer page)
    {
        this.page = page;
    }

    /**
     * @return total pages for the query
     */
    public Integer getTotal()
    {
        return total;
    }

    /**
     * @param total
     *          total pages for the query
     */
    public void setTotal(Integer total)
    {
        this.total = total;
    }

    /**
     * @return total number of records for the query. e.g. select count(*) from
     *         table
     */
    public Integer getRecords()
    {
        return records;
    }

    /**
     * @param record
     *          total number of records for the query. e.g. select count(*) from
     *          table
     */
    public void setRecords(Integer records)
    {
        this.records = records;

        if ((this.records > 0) && (this.rows > 0))
        {
            this.total = (int) Math.ceil((double) this.records / (double) this.rows);
        }
        else
        {
            this.total = 0;
        }
    }

    /**
     * @return sorting order
     */
    public String getSord()
    {
        return sord;
    }

    /**
     * @param sord
     *          sorting order
     */
    public void setSord(String sord)
    {
        this.sord = sord;
    }

    /**
     * @return get index row - i.e. user click to sort.
     */
    public String getSidx()
    {
        return sidx;
    }

    /**
     * @param sidx
     *          get index row - i.e. user click to sort.
     */
    public void setSidx(String sidx)
    {
        this.sidx = sidx;
    }

    public void setSearchField(String searchField)
    {
        this.searchField = searchField;
    }

    public void setSearchString(String searchString)
    {
        this.searchString = searchString;
    }

    public void setSearchOper(String searchOper)
    {
        this.searchOper = searchOper;
    }

    public void setLoadonce(boolean loadonce)
    {
        this.loadonce = loadonce;
    }

    public void setSession(Map<String, Object> session)
    {
        this.session = session;
    }

    public Integer getTotalrows(Integer totalrows)
    {
        return this.totalrows;
    }

    public void setTotalrows(Integer totalrows)
    {
        this.totalrows = totalrows;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public String getOper()
    {
        return oper;
    }

    public void setOper(String oper)
    {
        this.oper = oper;
    }

    public String execute() throws Exception
    {
        System.out.println("\n\n***WARNING!! BaseStrutsAction() - execute() called but not overloaded.");

        return (noOp());
    }

    public String getStatusMessage()
    {
        return statusMessage.toString();
    }

    protected void addMessage(String str)
    {
        statusMessage.append(str).append("<br/>");
    }

    /**
     * Helper method use to paginate the provided list
     * 
     * @param list
     */
    protected void paginate(Collection list)
    {
        int listSize 		= list.size();
        Object asArray[]	= list.toArray();
        
        paginatedList = new ArrayList();

        totalrows = listSize;
        total     = totalrows / rows;

        if ((totalrows % rows) > 0)
        {
            total = total + 1;
        }

        if (page > total)
        {
            page = total;
        }

        int from = rows * (page - 1);
        int to = from + rows;

        if (to > listSize)
        {
            to = listSize;
        }

        if (from > listSize)
        {
            from = listSize - (from - listSize);
        }

        for (int index = from; index < to; index++)
        {
            paginatedList.add(asArray[index]);
            records++;
        }

        records = list.size();

    }

    /**
     * used to return pagination related data
     * @return
     */
    public Map<String, Object> getPaginatedList()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", page);
        map.put("total", total);
        map.put("records", records);
        map.put("rows", paginatedList);

        return (map);
    }

    public Long[] getChildIds() {
    	return childIds;
    }
    
	public void setChildIds( Long[] ids ) {
		childIds = ids;
	}

	public Long getChildId() {
		return childId;
	}

	public void setChildId( Long id ) {
		childId = id;
	}

// attributes
	protected Long[] childIds 				= null;
	protected Long childId 					= null;

    private List paginatedList = null;
    private String [] selections = null;
    private String selection = null;
    private boolean loaded = false;
    private String cacheOnly = "false";

    //private ApplicationUSOM appUSOM = new ApplicationUSOM();
    private String roleName = null;

    // get how many rows we want to have into the grid - rowNum attribute in the
    // grid
    private Integer rows = 20;

    // Get the requested page. By default grid sets this to 1.
    private Integer page = 1;

    // sorting order - asc or desc
    private String sord;

    // get index row - i.e. user click to sort.
    private String sidx;

    // Search Field
    private String searchField;

    // The Search String
    private String searchString;

    // Limit the result when using local data, value form attribute rowTotal
    private Integer totalrows = 0;

    // he Search Operation
    // ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
    private String searchOper;

    // Your Total Pages
    private Integer total = 0;

    // All Records
    private Integer records = 0;
    private boolean loadonce = false;
    private Map<String, Object> session;
    private String oper = "edit";
    protected int id = 0;
    private StringBuilder statusMessage = new StringBuilder();
    static protected final String SUCCESS = "success";
    static protected final String ERROR = "error";
}
/*
 * $Author$
 * $Date$
 * $LastChangedDate$
 * $LastChangedRevision$
 */
