package gov.nasa.pds.api.registry.controllers;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.pds.api.registry.business.ProductVersionSelector;

public class URIParameters
{
	private List<String> fields = new ArrayList<String>();
	private String identifier = null;
	private List<String> keywords = new ArrayList<String>();
	private Integer limit = Integer.valueOf(10);
	private String query = null;
	private ProductVersionSelector selector = ProductVersionSelector.LATEST;
	private List<String> sort = new ArrayList<String>();
	private Integer start = Integer.valueOf(0);
	private Boolean summanryOnly = Boolean.valueOf(false);
	
	public List<String> getFields() { return fields; }
	public String getIdentifier() { return identifier; }
	public List<String> getKeywords() { return keywords; }
	public Integer getLimit() { return limit; }
	public String getQuery() { return query; }
	public ProductVersionSelector getSelector() { return selector; }
	public List<String> getSort() { return sort; }
	public Integer getStart() { return start; }
	public Boolean getSummanryOnly() { return summanryOnly; }

	public URIParameters setFields(List<String> fields)
	{
		this.fields = fields;
		return this;
	}

	public URIParameters setIdentifier(String identifier)
	{
		this.identifier = identifier;
		return this;
	}

	public URIParameters setKeywords(List<String> keywords)
	{
		this.keywords = keywords;
		return this;
	}

	public URIParameters setLimit(Integer limit)
	{
		this.limit = limit;
		return this;
	}

	public URIParameters setQuery(String query)
	{
		this.query = query;
		return this;
	}

	public URIParameters setSelector(ProductVersionSelector selector)
	{
		this.selector = selector;
		return this;
	}

	public URIParameters setSort(List<String> sort)
	{
		this.sort = sort;
		return this;
	}

	public URIParameters setStart(Integer start)
	{
		this.start = start;
		return this;
	}

	public URIParameters setSummanryOnly(Boolean summanryOnly)
	{
		this.summanryOnly = summanryOnly;
		return this;
	}
}
