/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa;

import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

@Category(IntegrationTest.class)
public class PdfAIndirectResourcesTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/pdfs/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAIndirectResourcesTest/";

    @Before
    public void configure() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfAConformanceLogMessageConstant.CATALOG_SHOULD_CONTAIN_LANG_ENTRY)
    })
    public void indirectResources01Test() throws IOException {
        String fileName = destinationFolder + "indirectResources01Test.pdf";
        PdfADocument pdfDoc = new PdfADocument(new PdfReader(sourceFolder + "indirectResources01.pdf"), new PdfWriter(fileName));
        pdfDoc.close();
    }

    @Test
    public void indirectResources02Test() throws IOException {
        String fileName = destinationFolder + "indirectResources02Test.pdf";

        PdfWriter writer = new CustomPdfWriter(fileName, 19);
        PdfADocument pdfDoc = new PdfADocument(new PdfReader(sourceFolder + "indirectResources02.pdf"), writer);
        pdfDoc.close();
    }

    private static class CustomPdfWriter extends PdfWriter {
        private int objectToFlushNumber;

        public CustomPdfWriter(String filename, int objectToFlushNumber) throws FileNotFoundException {
            super(filename);
            this.objectToFlushNumber = objectToFlushNumber;
        }

        @Override
        protected void flushWaitingObjects(Set<PdfIndirectReference> forbiddenToFlush) {
            // Because of flushing order in PdfDocument is uncertain, flushWaitingObjects() method is overridden
            // to simulate the issue when the certain PdfObject A, that exists in the Catalog entry and in the resources
            // of another PdfObject B, is flushed before the flushing of the PdfObject B.
            super.document.getPdfObject(objectToFlushNumber).flush();
            super.flushWaitingObjects(forbiddenToFlush);
        }
    }
}
