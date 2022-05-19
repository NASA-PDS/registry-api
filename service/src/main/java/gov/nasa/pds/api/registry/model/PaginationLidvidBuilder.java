package gov.nasa.pds.api.registry.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.nasa.pds.api.registry.LidvidsContext;

class PaginationLidvidBuilder implements Pagination<String>
{
	public int current = 0;
	final public int limit;
	final public int start;
	final public List<String> page = new ArrayList<String>();

	PaginationLidvidBuilder (LidvidsContext bounds)
	{
		this.limit = bounds.getLimit();
		this.start = bounds.getStart();
	}

	void addAll (List<String> data)
	{
		int remaining = this.limit - this.page.size();

		if (this.start <= this.current && 0 < remaining)
		{
			if (data.size() <= remaining) page.addAll(data);
			else page.addAll(data.subList(0, remaining));
		}
		this.current += data.size();		
	}

	void add (Object sourceMapValue) { this.addAll(this.convert(null)); }

	List<String> convert (Object sourceMapValue)
	{
		@SuppressWarnings("unchecked")
		List<String> values = sourceMapValue instanceof List ? (List<String>)sourceMapValue : Arrays.asList((String)sourceMapValue);
		return values;
	}

	@Override
	public int limit() { return this.limit; }

	@Override
	public List<String> page() { return this.page; }

	@Override
	public int size() { return this.page.size(); }

	@Override
	public int start() { return this.start; }

	@Override
	public int total() { return this.current; }

}
