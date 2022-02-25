package gov.nasa.pds.api.model.xml;

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;

import gov.nasa.pds.api.registry.serializer.Pds4XmlProductSerializer;

public class NamespaceXmlFactory extends XmlFactory
{
	private static final Logger log = LoggerFactory.getLogger(NamespaceXmlFactory.class);
	private static final long serialVersionUID = 1L;

    public NamespaceXmlFactory() {}

    @Override
    protected XMLStreamWriter _createXmlWriter(IOContext ctxt, Writer w) throws IOException
    {
        XMLStreamWriter writer = super._createXmlWriter(ctxt, w);
        try { writer.setPrefix(Pds4XmlProductSerializer.NAMESPACE_PREFIX, Pds4XmlProductSerializer.NAMESPACE_URL); }
        catch (XMLStreamException e) { log.error("Could not set the namespace prefix", e); }
        return new XMLStreamWriterWithNamespace(writer);
    }
}