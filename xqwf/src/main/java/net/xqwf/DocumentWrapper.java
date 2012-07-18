package net.xqwf;

import java.io.IOException;
import java.io.Serializable;

import net.xqwf.helper.DocumentHelper;
import nu.xom.Document;
import nu.xom.Serializer;

public class DocumentWrapper implements Serializable {
	
	private final static long serialVersionUID = 1L;
	
	private Document document = null;
	
	public DocumentWrapper() {
		document = DocumentHelper.createEmptyDocument();
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		Serializer serializer = new Serializer(out);
		serializer.write(document);
		serializer.flush();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException {
		document = DocumentHelper.createDocumentFromStream(in);
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document processedDocument) {
		document = processedDocument;
	}
}
