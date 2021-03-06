package com.elastacloud.storm.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.elastacloud.storm.*;
import com.elastacloud.storm.interfaces.IServiceBusQueueDetail;
import com.elastacloud.storm.interfaces.IServiceBusTopicDetail;
import com.elastacloud.storm.ServiceBusTopicSubscriptionSpout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestServiceBusSpout {
    ServiceBusQueueSpout serviceBusSpout;
    ServiceBusTopicSubscriptionSpout serviceBusTopicSubscriptionSpout;

    @Mock
    IServiceBusQueueDetail serviceBusQueueMock;

    @Mock
    IServiceBusTopicDetail serviceBusTopicMock;

    @Before
    public void setUp() {
        serviceBusSpout = new ServiceBusQueueSpout(serviceBusQueueMock);
        serviceBusTopicSubscriptionSpout = new ServiceBusTopicSubscriptionSpout(serviceBusTopicMock);
    }

    @After
    public void tearDown()  {
        serviceBusSpout = null;
        serviceBusTopicSubscriptionSpout = null;
        serviceBusQueueMock = null;
    }

    /* Tests for service bus queue */
    @Test(expected=ServiceBusSpoutException.class)
    public void TestIncorrectConnectionString() throws ServiceBusSpoutException {
        ServiceBusQueueConnection connection = new ServiceBusQueueConnection("test;this", null);
        connection.getConnectionString();
    }

    @Test
    public void TestCorrectConnectionString() throws ServiceBusSpoutException {
        ServiceBusQueueConnection connection = new ServiceBusQueueConnection("test;this;thing", null);
        assertEquals("test;this;thing", connection.getConnectionString());
    }

    @Test(expected=ServiceBusSpoutException.class)
    public void TestInvalidQueueName() throws ServiceBusSpoutException  {
        ServiceBusQueueConnection connection = new ServiceBusQueueConnection("test;this", "sd");
        connection.getQueueName();
    }

    @Test
    public void TestConnectSuccess() throws ServiceBusSpoutException    {
        when(serviceBusQueueMock.getQueueName()).thenReturn("thequeue");
        when(serviceBusQueueMock.getConnectionString()).thenReturn("r;r;e");
        when(serviceBusQueueMock.isConnected()).thenReturn(true);
        when(serviceBusQueueMock.getNextMessageForSpout()).thenReturn(null);
        serviceBusSpout.open(null, null, new FakeSpoutOutputCollector(new FakeSpoutOutputDelegate()));
        serviceBusSpout.nextTuple();

        verify(serviceBusQueueMock, times(1)).connect();
        verify(serviceBusQueueMock, times(1)).isConnected();
        assertTrue(serviceBusSpout.isConnected());
        assertEquals(0, serviceBusSpout.getProcessedMessageCount());
    }

    @Test
    public void TestConnectFail() throws ServiceBusSpoutException    {
        when(serviceBusQueueMock.getQueueName()).thenReturn(null);
        when(serviceBusQueueMock.getConnectionString()).thenReturn("r;r");
        when(serviceBusQueueMock.isConnected()).thenReturn(false);
        serviceBusSpout.open(null, null, new FakeSpoutOutputCollector(new FakeSpoutOutputDelegate()));
        serviceBusSpout.nextTuple();

        verify(serviceBusQueueMock, times(1)).connect();
        verify(serviceBusQueueMock, times(1)).isConnected();
        assertFalse(serviceBusSpout.isConnected());
        assertEquals(0, serviceBusSpout.getProcessedMessageCount());
    }

    /* Tests for subscription-topic */
    @Test(expected=ServiceBusSpoutException.class)
    public void TestIncorrectConnectionStringTopic() throws ServiceBusSpoutException {
        ServiceBusTopicConnection connection = new ServiceBusTopicConnection("test;this", null, null, null);
        connection.getConnectionString();
    }

    @Test
    public void TestCorrectConnectionStringTopic() throws ServiceBusSpoutException {
        ServiceBusTopicConnection connection = new ServiceBusTopicConnection("test;this;thing", null, null, null);
        assertEquals("test;this;thing", connection.getConnectionString());
    }

    @Test(expected=ServiceBusSpoutException.class)
    public void TestInvalidTopicName() throws ServiceBusSpoutException  {
        ServiceBusTopicConnection connection = new ServiceBusTopicConnection("test;this", "sd", null, null);
        connection.getTopicName();
    }

    @Test
    public void TestCorrectSubscription() throws ServiceBusSpoutException  {
        ServiceBusTopicConnection connection = new ServiceBusTopicConnection("test;this;this", "sd123", null, null);
        assertEquals("sd123sub", connection.getSubscriptionName());
    }

    @Test

    public void TestTopicConnectSuccess() throws ServiceBusSpoutException    {
        when(serviceBusTopicMock.getTopicName()).thenReturn("thetopic");
        when(serviceBusTopicMock.getConnectionString()).thenReturn("rsdf;rsdf;esdf");
        when(serviceBusTopicMock.isConnected()).thenReturn(true);
        when(serviceBusTopicMock.getNextMessageForSpout()).thenReturn(null);
        serviceBusTopicSubscriptionSpout.open(null, null, new FakeSpoutOutputCollector(new FakeSpoutOutputDelegate()));
        serviceBusTopicSubscriptionSpout.nextTuple();

        verify(serviceBusTopicMock, times(1)).connect();
        verify(serviceBusTopicMock, times(1)).isConnected();

        assertTrue(serviceBusTopicSubscriptionSpout.isConnected());
        assertEquals(0, serviceBusTopicSubscriptionSpout.getProcessedMessageCount());
    }

    @Test
    public void TestTopicConnectFail() throws ServiceBusSpoutException    {
        when(serviceBusTopicMock.getTopicName()).thenReturn(null);
        when(serviceBusTopicMock.getConnectionString()).thenReturn("r;r");
        when(serviceBusTopicMock.isConnected()).thenReturn(true);
        serviceBusTopicSubscriptionSpout.open(null, null, new FakeSpoutOutputCollector(new FakeSpoutOutputDelegate()));
        serviceBusTopicSubscriptionSpout.nextTuple();

        verify(serviceBusTopicMock, times(1)).connect();
        verify(serviceBusTopicMock, times(1)).isConnected();
        assertFalse(serviceBusSpout.isConnected());
        assertEquals(0, serviceBusSpout.getProcessedMessageCount());
    }
}

