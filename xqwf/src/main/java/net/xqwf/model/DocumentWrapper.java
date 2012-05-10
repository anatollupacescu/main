package net.xqwf.model;

import java.io.IOException;
import java.io.Serializable;

import net.xqwf.helper.DocumentHelper;
import nu.xom.Document;
import nu.xom.Serializer;

public class DocumentWrapper implements Serializable {
	private static final long serialVersionUID = 1L;

	private nu.xom.Document document = null;

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
