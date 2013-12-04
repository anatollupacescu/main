package net.sandbox;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import net.sandbox.segment.PathSegment;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.junit.Test;

public class MainTest {

	private Main main;
	
	public MainTest() throws ODataException {
		main = new Main();
	}
	
	@Test
	public final void test() throws MalformedURLException {
		final String path = "Employees(guid=1)/preferences/$value?$expand=account";
		PathSegment segments = main.lookupSegments(path);
		assertEquals(true, segments.hasNext());
		assertEquals(false, segments.getKeyMap().isEmpty());
	}

}
