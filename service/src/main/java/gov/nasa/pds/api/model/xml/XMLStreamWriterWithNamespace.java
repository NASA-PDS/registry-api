package gov.nasa.pds.api.model.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import gov.nasa.pds.api.engineering.serializer.Pds4XmlProductSerializer;

class XMLStreamWriterWithNamespace implements XMLStreamWriter
{
	final private XMLStreamWriter actual;

	public XMLStreamWriterWithNamespace (XMLStreamWriter actual) { this.actual = actual; }

	@Override
	public void close() throws XMLStreamException { this.actual.close(); }

	@Override
	public void flush() throws XMLStreamException { this.actual.flush(); }

	@Override
	public NamespaceContext getNamespaceContext() { return this.actual.getNamespaceContext(); }

	@Override
	public String getPrefix(String uri) throws XMLStreamException { return this.actual.getPrefix(uri); }

	@Override
	public Object getProperty(String name) throws IllegalArgumentException { return this.actual.getProperty(name); }

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException { this.actual.setDefaultNamespace(uri); }

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException { this.actual.setNamespaceContext(context); }

	@Override
	public void setPrefix(String arg0, String arg1) throws XMLStreamException { this.actual.setPrefix(arg0, arg1); }

	@Override
	public void writeAttribute(String arg0, String arg1) throws XMLStreamException
	{ this.actual.writeAttribute(Pds4XmlProductSerializer.NAMESPACE_URL, arg0, arg1); }

	@Override
	public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException
	{ this.actual.writeAttribute(arg0, arg1, arg2); }

	@Override
	public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException
	{ this.actual.writeAttribute(arg0, arg1, arg2, arg3); }

	@Override
	public void writeCData(String data) throws XMLStreamException { this.actual.writeCData(data); }

	@Override
	public void writeCharacters(String text) throws XMLStreamException { this.actual.writeCharacters(text); }

	@Override
	public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException
	{ this.actual.writeCharacters(arg0, arg1, arg2); }

	@Override
	public void writeComment(String data) throws XMLStreamException { this.actual.writeComment(data); }

	@Override
	public void writeDTD(String dtd) throws XMLStreamException { this.actual.writeDTD(dtd); }

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException { this.actual.writeDefaultNamespace(namespaceURI); }

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException
	{ this.actual.writeEmptyElement(Pds4XmlProductSerializer.NAMESPACE_URL, localName); }

	@Override
	public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException { this.actual.writeEmptyElement(arg0, arg1); }

	@Override
	public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException { this.actual.writeEmptyElement(arg0, arg1, arg2); }

	@Override
	public void writeEndDocument() throws XMLStreamException { this.actual.writeEndDocument(); }

	@Override
	public void writeEndElement() throws XMLStreamException { this.actual.writeEndElement(); }

	@Override
	public void writeEntityRef(String name) throws XMLStreamException { this.actual.writeEntityRef(name); }

	@Override
	public void writeNamespace(String arg0, String arg1) throws XMLStreamException 
	{ this.actual.writeNamespace(arg0, arg1); }

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException 
	{ this.actual.writeProcessingInstruction(target); }

	@Override
	public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException
	{ this.actual.writeProcessingInstruction(arg0, arg1); }

	@Override
	public void writeStartDocument() throws XMLStreamException { this.actual.writeStartDocument(); }

	@Override
	public void writeStartDocument(String version) throws XMLStreamException { this.actual.writeStartDocument(version); }

	@Override
	public void writeStartDocument(String arg0, String arg1) throws XMLStreamException
	{ this.actual.writeStartDocument(arg0, arg1); }

	@Override
	public void writeStartElement(String localName) throws XMLStreamException
	{ this.actual.writeStartElement(Pds4XmlProductSerializer.NAMESPACE_URL,localName); }

	@Override
	public void writeStartElement(String arg0, String arg1) throws XMLStreamException
	{ this.actual.writeStartElement(Pds4XmlProductSerializer.NAMESPACE_URL, arg1);}

	@Override
	public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException
	{ this.actual.writeStartElement(arg0, arg1, arg2); }

}
