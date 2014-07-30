/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.aeron.examples;

import uk.co.real_logic.aeron.Aeron;
import uk.co.real_logic.aeron.Subscription;
import uk.co.real_logic.aeron.common.CloseHelper;
import uk.co.real_logic.aeron.driver.MediaDriver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static uk.co.real_logic.aeron.examples.ExampleUtil.printStringMessage;

/**
 * Example Aeron subscriber application
 */
public class ExampleSubscriber
{
    private static final int STREAM_ID = ExampleConfiguration.STREAM_ID;
    private static final String CHANNEL = ExampleConfiguration.CHANNEL;
    private static final int FRAME_COUNT_LIMIT = ExampleConfiguration.FRAME_COUNT_LIMIT;
    private static final boolean EMBEDDED_MEDIA_DRIVER = ExampleConfiguration.EMBEDDED_MEDIA_DRIVER;

    public static void main(final String[] args) throws Exception
    {
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        final Aeron.Context aeronContext = new Aeron.Context();

        final MediaDriver driver = EMBEDDED_MEDIA_DRIVER ? ExampleUtil.createEmbeddedMediaDriver() : null;

        aeronContext.newConnectionHandler(ExampleUtil::printNewConnection);
        aeronContext.inactiveConnectionHandler(ExampleUtil::printInactiveConnection);

        final Aeron aeron = ExampleUtil.createAeron(aeronContext, executor);

        System.out.println("Subscribing to " + CHANNEL + " on stream Id " + STREAM_ID);

        // subscription for channel Id 1
        try (final Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID, printStringMessage(STREAM_ID)))
        {
            // run the subscriber thread from here
            ExampleUtil.subscriberLoop(FRAME_COUNT_LIMIT).accept(subscription);
        }

        CloseHelper.quietClose(aeron);
        CloseHelper.quietClose(driver);
        executor.shutdown();
    }
}
