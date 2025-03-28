/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.ecosystem.ITextTestEvent;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ProductEventHandlerIntegrationTest extends ExtendedITextTest {
    private PrintStream outBackup;

    @Before
    public void initTest() {
        outBackup = System.out;
        ProductEventHandler.INSTANCE.clearProcessors();
    }

    @After
    public void afterEach() {
        System.setOut(outBackup);
        ProductProcessorFactoryKeeper.restoreDefaultProductProcessorFactory();
        ProductEventHandler.INSTANCE.clearProcessors();
    }

    @Test
    public void removeAGPLLoggingTest() {
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        EventManager.acknowledgeAgplUsageDisableWarningMessage();
        for (int i = 0; i < 10001; i++) {

            ProductEventHandler handler = ProductEventHandler.INSTANCE;

            SequenceId sequenceId = new SequenceId();

            Assert.assertTrue(handler.getEvents(sequenceId).isEmpty());
            ITextTestEvent event = new ITextTestEvent(sequenceId, null, "test-event",
                    ProductNameConstant.ITEXT_CORE);
            EventManager.getInstance().onEvent(event);

            ConfirmEvent confirmEvent = new ConfirmEvent(sequenceId, event);
            EventManager.getInstance().onEvent(confirmEvent);

            Assert.assertEquals(1, handler.getEvents(sequenceId).size());
            Assert.assertTrue(handler.getEvents(sequenceId).get(0) instanceof ConfirmedEventWrapper);
            Assert.assertEquals(event, ((ConfirmedEventWrapper) handler.getEvents(sequenceId).get(0)).getEvent());
        }
        Assert.assertEquals("", testOut.toString());
    }
}
