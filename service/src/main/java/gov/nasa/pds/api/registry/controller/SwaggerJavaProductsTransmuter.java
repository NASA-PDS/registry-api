package gov.nasa.pds.api.registry.controller;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import gov.nasa.pds.api.base.ClassesApi;
import gov.nasa.pds.api.base.ProductsApi;
import gov.nasa.pds.api.registry.ControlContext;
import gov.nasa.pds.api.registry.model.ProductVersionSelector;

abstract class SwaggerJavaProductsTransmuter extends SwaggerJavaClassesTransmuter
		implements ControlContext, ProductsApi, ClassesApi {

	public Optional<NativeWebRequest> getRequest() {
		return Optional.empty();
	}

	@Override
	public ResponseEntity<Object> productList(@Valid List<String> fields, @Valid List<String> keywords,
			@Min(0) @Valid Integer limit, @Valid String q, @Valid List<String> sort, 
                        @Min(0) @Valid Integer start, @Valid String node) {
		return super.classList("any", fields, keywords, limit, q, sort, start, node);
	}

	@Override
	public ResponseEntity<Object> productMemberOf(String identifier, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, 
                        @Min(0) @Valid Integer start, @Valid String node) {
		return this.processs(new Member(false, false), new URIParameters().setIdentifier(identifier)
                    .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMemberOfOf(String identifier, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, 
                        @Min(0) @Valid Integer start, @Valid String node) {
		return this.processs(new Member(false, true), new URIParameters().setIdentifier(identifier)
                    .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMemberOfOfVers(String identifier, String versions, 
                        @Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort, 
                        @Min(0) @Valid Integer start, @Valid String node) {
		return this.processs(new Member(false, true), new URIParameters().setIdentifier(identifier)
		    .setFields(fields).setLimit(limit).setSort(sort).setStart(start).setVersion(versions)
		    .setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMemberOfVers(String identifier, String versions, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
                        @Valid String node) {
		return this.processs(new Member(false, false), new URIParameters().setIdentifier(identifier).setFields(fields)
				.setLimit(limit).setSort(sort).setStart(start).setVersion(versions).setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMembers(String identifier, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
			@Valid String node) {
		return this.processs(new Member(true, false), new URIParameters().setIdentifier(identifier).setFields(fields)
				.setLimit(limit).setSort(sort).setStart(start).setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMembersMembers(String identifier, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
			@Valid String node) {
		return this.processs(new Member(true, true), new URIParameters().setIdentifier(identifier).setFields(fields)
				.setLimit(limit).setSort(sort).setStart(start).setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMembersMembersVers(String identifier, String versions,
			@Valid List<String> fields, @Min(0) @Valid Integer limit, @Valid List<String> sort,
			@Min(0) @Valid Integer start, @Valid String node) {
		return this.processs(new Member(true, true), new URIParameters().setIdentifier(identifier).setFields(fields)
		    .setLimit(limit).setSort(sort).setStart(start).setVersion(versions).setNode(node));
	}

	@Override
	public ResponseEntity<Object> productMembersVers(String identifier, String versions, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
			@Valid String node) {
		return this.processs(new Member(true, false), new URIParameters().setIdentifier(identifier).setFields(fields)
		    .setLimit(limit).setSort(sort).setStart(start).setVersion(versions).setNode(node));
	}

	@Override
	public ResponseEntity<Object> selectByLidvid(String identifier, @Valid List<String> fields, @Valid String node) {
		return this.processs(new Standard(), new URIParameters().setIdentifier(identifier).setFields(fields)
		    .setNode(node));
	}

	@Override
	public ResponseEntity<Object> selectByLidvidAll(String identifier, @Valid List<String> fields,
			@Min(0) @Valid Integer limit, @Valid List<String> sort, @Min(0) @Valid Integer start,
			@Valid String node) {
		return this.processs(new Standard(), new URIParameters().setIdentifier(identifier).setFields(fields)
		    .setLimit(limit).setSort(sort).setStart(start).setVersion(ProductVersionSelector.ALL)
		    .setNode(node));
	}

	@Override
	public ResponseEntity<Object> selectByLidvidLatest(String identifier, @Valid List<String> fields,
			@Valid String node) {
		return this.processs(new Standard(), new URIParameters().setIdentifier(identifier).setFields(fields)
		    .setVersion(ProductVersionSelector.LATEST).setNode(node));
	}
}
