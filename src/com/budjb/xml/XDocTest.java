package com.budjb.xml;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.*;

public class XDocTest
{
	XDoc testDoc;
	
	@Before
	public void setUp() throws Exception
	{
		testDoc = new XDoc("doc").attr("source", "http://budjb.com")
				.value("Hello ").start("bold").attr("style", "blinking")
				.value("World").end().value("!").start("br").end()
				.start("bold").value("Cool").end().start("span")
				.value("Ce\u00e7i est \"une\" id\u00e9e").end().start("struct")
				.start("name").value("John").end().start("last").value("Doe")
				.end().end();
	}

	@Test
	public void xmlSerialization1()
	{
		assertEquals(testDoc.toString(), "<doc source=\"http://budjb.com\">Hello <bold style=\"blinking\">World</bold>!<br/><bold>Cool</bold><span>Ce\u00e7i est \"une\" id\u00e9e</span><struct><name>John</name><last>Doe</last></struct></doc>");
	}

	@Test
	public void elementCount()
	{
		assertEquals(2, testDoc.at("bold").length());
	}
	
	@Test
	public void elementAccessIndexed()
	{
		assertEquals("Cool", testDoc.at(4).getContents());
	}
	
	@Test
	public void elementAccessXPathFirst()
	{
		assertEquals("World", testDoc.at("bold").getContents());
	}
	
	@Test
	public void elementAccessXPathSecond()
	{
		assertEquals("Cool", testDoc.at("bold").getNext().getContents());
	}
	
	@Test
	public void elementAccessXPathWithIndex()
	{
		assertEquals("Cool", testDoc.at("bold[2]").getContents());
	}
	
	@Test
	public void xmlAdd1()
	{
		testDoc.add(new XDoc("subdoc").start("tag").value("value").end());
        assertEquals("<doc source=\"http://budjb.com\">Hello <bold style=\"blinking\">World</bold>!<br/><bold>Cool</bold><span>Ce\u00e7i est \"une\" id\u00e9e</span><struct><name>John</name><last>Doe</last></struct><subdoc><tag>value</tag></subdoc></doc>", testDoc.toString());
	}
	
	@Test
	public void xmlAdd2()
	{
		testDoc.at("bold").start("italic").value(1).end().start("underline").value(2).end();
		assertEquals("<doc source=\"http://budjb.com\">Hello <bold style=\"blinking\">World<italic>1</italic><underline>2</underline></bold>!<br/><bold>Cool</bold><span>Ce\u00e7i est \"une\" id\u00e9e</span><struct><name>John</name><last>Doe</last></struct></doc>", testDoc.toString());
	}
	
	@Test
	public void xmlClone1() throws CloneNotSupportedException
	{
		XDoc doc = testDoc.clone();
		assertEquals("<doc source=\"http://budjb.com\">Hello <bold style=\"blinking\">World</bold>!<br/><bold>Cool</bold><span>Ce\u00e7i est \"une\" id\u00e9e</span><struct><name>John</name><last>Doe</last></struct></doc>", doc.toString());
	}
	
	@Test
	public void xmlClone2() throws CloneNotSupportedException
	{
		XDoc doc = testDoc.at("struct").clone();
		assertEquals("<struct><name>John</name><last>Doe</last></struct>", doc.toString());
	}
	
	@Test
	public void xmlEndAll1()
	{
		XDoc doc = testDoc.at("struct").endAll();
		assertEquals("struct", doc.getName());
	}
	
	@Test
	public void xmlEndAll2()
	{
		XDoc doc = testDoc.at("struct").start("outer").start("inner").endAll();
		assertEquals("struct", doc.getName());
	}
	
	@Test
	public void xmlEnd()
	{
		XDoc doc = testDoc.at("struct").start("outer").start("inner").end().end();
		assertEquals("struct", doc.getName());
	}
	
	@Test(expected = IllegalStateException.class)
	public void XmlEndFail()
	{
		testDoc.at("struct").start("outer").start("inner").end().end().end();
	}
	
	@Test
	public void xmlAddBefore()
	{
		XDoc doc = new XDoc("root").start("first").end().start("second").end().start("third").end();
        doc.at("second").addBefore(new XDoc("node"));
        assertEquals("<root><first/><node/><second/><third/></root>", doc.toString());
	}
	
	@Test
	public void xmlAddAfter()
	{
		XDoc doc = new XDoc("root").start("first").end().start("second").end().start("third").end();
        doc.at("second").addAfter(new XDoc("node"));
        assertEquals("<root><first/><second/><node/><third/></root>", doc.toString());
	}
	
	@Test
	public void loadEmptyXml()
	{
		assertEquals(true, XDoc.load("").isEmpty());
	}
	
	@Test
	public void loadNonXml()
	{
		assertEquals(true, XDoc.load("hello").isEmpty());
	}
	
	@Test
	public void loadXml()
	{
		String xml = "<root>mixed<inner>text</inner></root>";
		assertEquals(xml, XDoc.load(xml).toString());
	}
	
	@Test
	public void renderXml()
	{
		XDoc doc = new XDoc("html").start("body").start("a").attr("href", "http://foo.bar?q=1&p=2").value("test").end().end();
		assertEquals("<html><body><a href=\"http://foo.bar?q=1&amp;p=2\">test</a></body></html>", doc.toString());
	}
	
	@Test
	public void xPost()
	{
		XDoc doc = new XDoc("test");
        doc.insertValueAt("", "text");
        doc.insertValueAt("@id", "123");
        doc.insertValueAt("foo/a", "a");
        doc.insertValueAt("foo/b", "b");
        doc.insertValueAt("foo/@key", "value");
        doc.insertValueAt("foo[3]/c", "c");
        doc.insertValueAt("bar[3]/d", "d");
        doc.insertValueAt("foo[5]/e", "e");
        doc.insertValueAt("foo[5]/f", "f");
        
        assertEquals("<test id=\"123\">text<foo key=\"value\"><a>a</a><b>b</b></foo><foo/><foo><c>c</c></foo><bar/><bar/><bar><d>d</d></bar><foo/><foo><e>e</e><f>f</f></foo></test>", doc.toString());
	}
	
	@Test
	public void xmlContents()
	{
		XDoc doc = new XDoc("test");
		doc.value("<tag>text</tag>");
		String text = doc.getContents();
		assertEquals("<tag>text</tag>", text);
	}

	@Test
	public void xmlAsText1()
	{
		XDoc doc = new XDoc("test");
		String text = doc.at("foo").asText();
		assertEquals(null, text);
	}
	
	@Test
	public void xmlAsText2()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("zzz").value("2").end();
		String text = doc.at("foo").asText();
		assertEquals(null, text);
	}
	
	@Test
	public void xmlAsText3()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().elem("zzz");
		String text = doc.at("zzz").asText();
		assertEquals("", text);
	}

	@Test
	public void xmlAsInnerText()
	{
		XDoc doc = new XDoc("test").elem("a", "1").elem("a", "2").elem("a", "3").elem("a", "4").elem("a", "5");
		String text = doc.asInnerText();
		assertEquals("12345", text);
	}

	@Test
	public void replace1()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("bbb").value("2").end().start("ccc").value("3").end();
		doc.at("bbb").replaceValue("0");
		assertEquals("0", doc.at("bbb").getContents());
	}

	@Test
	public void replace2()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("bbb").value("2").end().start("ccc").value("3").end();
		doc.at("bbb").replace("empty");
		assertEquals("<test><aaa>1</aaa>empty<ccc>3</ccc></test>", doc.toString());
	}


	@Test
	public void replace3()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("bbb").value("2").end().start("ccc").value("3").end();
		doc.at("bbb").replace(new XDoc("ddd").value("0"));
		assertEquals("<test><aaa>1</aaa><ddd>0</ddd><ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void replace4()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("bbb").value("2").end().start("ccc").value("3").end();
		Object value = "empty";
		doc.at("bbb").replace(value);
		assertEquals("<test><aaa>1</aaa>empty<ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void replace5()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("bbb").value("2").end().start("ccc").value("3").end();
		XDoc value = new XDoc("ddd").value("0");
		doc.at("bbb").replace(value);
		assertEquals("<test><aaa>1</aaa><ddd>0</ddd><ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void replace6()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end().start("bbb").value("2").end().start("ccc").value("3").end();
		XDoc value = new XDoc("ddd").value("0");
		doc.replace(value);
		assertEquals("<ddd>0</ddd>", doc.toString());
	}
	
	@Test
	public void rename1()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).start("aaa").value("1").end().start("bbb").attr("attr", 2).value("2").end().start("ccc").value("3").end();
		doc.at("bbb").rename("foo");
		assertEquals("<test attr=\"1\"><aaa>1</aaa><foo attr=\"2\">2</foo><ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void rename2()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).start("aaa").value("1").end().start("bbb").attr("attr", 2).value("2").end().start("ccc").value("3").end();
		doc.rename("foo");
		assertEquals("<foo attr=\"1\"><aaa>1</aaa><bbb attr=\"2\">2</bbb><ccc>3</ccc></foo>", doc.toString());
	}

	@Test
	public void inFrontNodes()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).start("aaa").value("1").end();
		doc.addNodesInFront(new XDoc("bbb").attr("attr", 2).value("start").elem("ccc", "inner").value("end"));
		assertEquals("<test attr=\"1\">start<ccc>inner</ccc>end<aaa>1</aaa></test>", doc.toString());
	}

	@Test
	public void insertNodes()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).start("aaa").value("1").end();
		doc.addNodes(new XDoc("bbb").attr("attr", 2).value("start").elem("ccc", "inner").value("end"));
		assertEquals("<test attr=\"1\"><aaa>1</aaa>start<ccc>inner</ccc>end</test>", doc.toString());
	}

	@Test
	public void appendNodes()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).elem("aaa", "1").elem("bbb", "2").elem("ccc", "3");
		doc.at("bbb").addNodesAfter(new XDoc("zzz").attr("attr", 2).value("start").elem("xxx", "inner").value("end"));
		assertEquals("<test attr=\"1\"><aaa>1</aaa><bbb>2</bbb>start<xxx>inner</xxx>end<ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void appendText()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).elem("aaa", "1").elem("bbb", "2").elem("ccc", "3");
		doc.at("bbb").addAfter("zzz");
		assertEquals("<test attr=\"1\"><aaa>1</aaa><bbb>2</bbb>zzz<ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void prependNodes()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).elem("aaa", "1").elem("bbb", "2").elem("ccc", "3");
		doc.at("bbb").addNodesBefore(new XDoc("bbb").attr("attr", 2).value("start").elem("xxx", "inner").value("end"));
		assertEquals("<test attr=\"1\"><aaa>1</aaa>start<xxx>inner</xxx>end<bbb>2</bbb><ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void prependText()
	{
		XDoc doc = new XDoc("test").attr("attr", 1).elem("aaa", "1").elem("bbb", "2").elem("ccc", "3");
		doc.at("bbb").addBefore("zzz");
		assertEquals("<test attr=\"1\"><aaa>1</aaa>zzz<bbb>2</bbb><ccc>3</ccc></test>", doc.toString());
	}

	@Test
	public void parent1()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end();
		assertEquals(true, doc.getParent().isEmpty());
	}

	@Test
	public void parent2()
	{
		XDoc doc = new XDoc("test").start("aaa").value("1").end();
		doc.at("aaa").getParent().attr("attr", 1);
		assertEquals("<test attr=\"1\"><aaa>1</aaa></test>", doc.toString());
	}

	@Test
	public void parent3()
	{
		XDoc doc = new XDoc("test").start("aaa").value("start").elem("bbb").end();
		doc.at("aaa/bbb").getParent().value("end");
		assertEquals("<test><aaa>start<bbb/>end</aaa></test>", doc.toString());
	}
	
	@Test
	public void removeAll()
	{
		XDoc actual = new XDoc("test").attr("attr", 1).start("a").attr("attr", 2).start("aa").attr("attr", 3).attr("other", 4).value("test").end().end();
		XDoc expected = new XDoc("test").start("a").start("aa").attr("other", 4).value("test").end().end();
		actual.at("//@attr").removeAll();
		assertEquals(expected.toString(), actual.toString());
	}
	
	@Test
	public void documentEquality()
	{
		XDoc x = new XDoc("x").elem("a", "1").elem("b", "2");
		XDoc y = new XDoc("x").elem("a", "1").elem("b", "2");
		XDoc z = new XDoc("x").elem("a", "1").elem("b", "3");
		XDoc null1 = null;
		//XDoc null2 = null;
		
		assertTrue(x.equals(y));
		assertFalse(x.equals(z));
		assertFalse(x.equals(null1));
		
		// These aren't available since Java doesn't allow overriding of ==
		// The correct results are below if == did what I wanted it to.
		/*
		assertFalse(x == null1);     // false
		assertFalse(null1 == x);     // false
		assertFalse(x != null1);     // false
		assertTrue(null1 != x);      // true
		assertTrue(null1 == null2);  // true
		assertTrue(null1 == null);   // true
		assertFalse(null1 != null2); // false
		assertFalse(null1 != null);  //false
		assertTrue(x == y);          // true
		assertFalse(x == z);         // false
		assertTrue(x != z);          // true
		assertFalse(x != y);         //false
		*/
	}
	
	@Test
	public void canReturnToMarkerNode()
	{
		XDoc doc = new XDoc("test").start("a");
		Node marker = doc.asNode();
		doc.start("b").start("c").start("d");
		assertEquals("d", doc.getName());
		doc.end(marker);
		assertEquals("a", doc.getName());
	}

	@Test
	public void markerThrowsAtDocumentRoot()
	{
		XDoc doc = new XDoc("test").start("a");
		doc.start("b").start("c").start("d");
		Node marker = doc.asNode();
		doc.end();
		assertEquals("c", doc.getName());
		try {
			doc.end(marker);
		}
		catch(IllegalStateException e) {
			if(e.getMessage().equals("xdoc is at root position")) {
				return;
			}
			throw new AssertionError("threw wrong InvalidOperationException:" + e.getMessage());
		}
		throw new AssertionError("should have thrown an InvalidOperationException.");
	}
	
	@Test
	public void asTextOnCdataShouldReturnValue()
	{
		Document document = XDoc.getNewDocument();
		document.appendChild(document.createElement("test"));
		Element x = document.createElement("x");
		document.getDocumentElement().appendChild(x);
		x.appendChild(document.createCDATASection("blah"));
		XDoc doc = new XDoc(document);
		assertEquals("blah", doc.at("x").at(0).asText());
	}

	@Test
	public void asTextOnTextShouldReturnValue()
	{
		Document document = XDoc.getNewDocument();
		document.appendChild(document.createElement("test"));
		Element x = document.createElement("x");
		document.getDocumentElement().appendChild(x);
		x.appendChild(document.createTextNode("blah"));
		XDoc doc = new XDoc(document);
		assertEquals("blah", doc.at("x").at(0).asText());
	}
	
	@Test
	public void asTextOnElementWithOneCdataNodeShouldReturnValue()
	{
		Document document = XDoc.getNewDocument();
		document.appendChild(document.createElement("test"));
		Element x = document.createElement("x");
		document.getDocumentElement().appendChild(x);
		x.appendChild(document.createCDATASection("blah"));
		XDoc doc = new XDoc(document);
		assertEquals("blah", doc.at("x").asText());
	}
	
	@Test
	public void asTextOnElementWithOneTextNodeShouldReturnValue()
	{
		Document document = XDoc.getNewDocument();
		document.appendChild(document.createElement("test"));
		Element x = document.createElement("x");
		document.getDocumentElement().appendChild(x);
		x.appendChild(document.createTextNode("blah"));
		XDoc doc = new XDoc(document);
		assertEquals("blah", doc.at("x").asText());
	}
	
	@Test
	public void asTextOnAttributeShouldReturnValue()
	{
		XDoc doc = new XDoc("test").attr("x", "blah");
		assertEquals("blah", doc.at("@x").asText());
	}

	@Test
	public void canDefineAndRetrieveCDATA()
	{
		XDoc doc = new XDoc("test").start("x").cDataSection("blah").end();
		assertEquals("blah", doc.at("x").at(0).asText());
	}

	@Test
	public void canUseDocumentRootAsMarker()
	{
		XDoc doc = new XDoc("test");
		Node marker = doc.asNode();
		doc.start("a").start("b").start("c").start("d");
		assertEquals("d", doc.getName());
		doc.end(marker);
		assertEquals("test", doc.getName());
	}

	@Test
	public void newEmptySelection()
	{
		XDoc docs = XDoc.createSelection();
		assertTrue(docs.isEmpty());
	}

	@Test
	public void newSelectionOfTwoDocuments()
	{
		XDoc docs = XDoc.createSelection(new XDoc[] { new XDoc("foo"), new XDoc("bar") });

		assertEquals(2, docs.length());

		assertEquals("foo", docs.getFirst().getName());
		assertEquals("bar", docs.getNext().getName());
	}

	@Test
	public void constructorCopy()
	{
		XDoc foo = new XDoc("foo");
		XDoc bar = new XDoc(foo);

		assertEquals("foo", bar.getName());
	}

	@Test
	public void listLengthDocumentElementOnly()
	{
		XDoc doc = new XDoc("foo");
		assertEquals(1, doc.length());
	}

	@Test
	public void firstNext()
	{
		XDoc docs = XDoc.createSelection(new XDoc[] { new XDoc("foo"), new XDoc("bar") });

		assertEquals("bar", docs.getNext().getName());
		assertEquals("foo", docs.getNext().getFirst().getName());
	}

	@Test
	public void asInnerTextOnEmpty()
	{
		assertNull(XDoc.empty.asInnerText());
	}
}
