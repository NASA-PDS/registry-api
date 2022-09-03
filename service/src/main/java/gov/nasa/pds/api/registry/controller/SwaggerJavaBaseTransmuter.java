package gov.nasa.pds.api.registry.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;

abstract class SwaggerJavaBaseTransmuter
{
	abstract protected ResponseEntity<Object> processs (EndpointHandler handler, URIParameters parameters);

	public ResponseEntity<Object> groupReferencingId(
			String group,
			String identifier,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new GroupReferencingId(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start));
	}

	public ResponseEntity<Object> groupReferencingIdVers(
			String group,
			String identifier,
			String versions,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new GroupReferencingId(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setVersion(versions)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start));
	}

	public ResponseEntity<Object> idReferencingGroup(
			String group,
			String identifier,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new IdReferencingGroup(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start));
	}

	public ResponseEntity<Object> idReferencingGroupVers(
			String group,
			String identifier,
			String versions,
			@Valid List<String> fields,
			@Min(0) @Valid Integer limit,
			@Valid List<String> sort,
			@Min(0) @Valid Integer start)
	{
		return this.processs(new IdReferencingGroup(), new URIParameters()
				.setGroup(group)
				.setIdentifier(identifier)
				.setVersion(versions)
				.setFields(fields)
				.setLimit(limit)
				.setSort(sort)
				.setStart(start));
	}
}
