package com.fejkbiljett.android.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;
import com.fejkbiljett.android.tickets.UppsalaTicket;
import com.fejkbiljett.android.tickets.Ticket;
import com.fejkbiljett.android.tickets.Ticket.TicketException;

import junit.framework.TestCase;

public class UppsalaTicketTest extends TestCase {

	HashMap<String,Ticket> hmTicket = new HashMap<String,Ticket>();
	HashMap<String,Bundle> hmData = new HashMap<String,Bundle>();
	
	String[] okTickets = { "full", "reduced" };

	public UppsalaTicketTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		for (int i=0; i<okTickets.length; i++) {
			String ticket = okTickets[i];
					
			hmTicket.put(ticket, new UppsalaTicket());
			hmData.put(ticket, new Bundle());
			
			Bundle data = hmData.get(ticket);
			
			//Is price reduced?
			data.putBoolean("price_reduced", ticket.contains("reduced"));
		}
	}
	
	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#create(android.os.Bundle)}.
	 */
	public void testCreate() {
		for(int i=0;i<okTickets.length;i++)
		{
			String ticket = okTickets[i];
			Boolean createSuccess=true;
			try {
				hmTicket.get(ticket).create(
						hmData.get(ticket) );
			} catch (TicketException e) {
				createSuccess=false;
			}
			assertTrue("Ticket (" + ticket +") created.", createSuccess);
		}
	}

	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#getMessage()}.
	 */
	public void testGetMessage() {
		int [] prices = { 15, 25 };
		String expectedString;
		for(int i=0;i<okTickets.length;i++)
		{
			String ticket = okTickets[i];
			Bundle data = hmData.get(ticket);
			try {
				hmTicket.get(ticket).create(data);
			} catch (TicketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar cal = Calendar.getInstance();
			
			cal.add(Calendar.MINUTE, ( 90 ));
			String validTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
			String validDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			
			String [] message = hmTicket.get(ticket).getMessage().split("\n");
			
			long code = Long.parseLong(message[5].split(" ")[4]);
						
			expectedString = message[5].substring(message[5].length()-3) + " UL";
			assertEquals(expectedString,message[0]);
			
			assertEquals("", message[1]);
			
			expectedString = data.getBoolean("price_reduced")?"UU UNGDOM ":"UV VUXEN ";
			expectedString += "Giltig till " + validTime + " " + validDate;
			assertEquals(expectedString, message[2]);
			
			assertEquals("Stadsbuss",message[3]);
			assertEquals("", message[4]);
			
			int price = data.getBoolean("price_reduced")?prices[0]:prices[1];
			expectedString = price + " SEK (6% MOMS) " + code;
			assertEquals(expectedString, message[5]);
			
			assertEquals("", message[6]);
			assertEquals('E',message[7].charAt(0));
			assertEquals('E',message[8].charAt(0));
			assertEquals('E',message[9].charAt(0));
			assertEquals("EEEEEEEEEE",message[10]);
			
			String codeStr = message[7].substring(1) + 
							 message[8].substring(1) + 
							 message[9].substring(1, message[9].length()-3);
			
			assertEquals(code, Long.parseLong(Utils.aeoxToHex(codeStr),16));
			}
	}

	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#getSender()}.
	 */
	public void testGetSender() {
		;
		for(int i=0;i<okTickets.length;i++)
		{
			String ticket = okTickets[i];
			Bundle data = hmData.get(ticket);
			try {
				hmTicket.get(ticket).create(data);
			} catch (TicketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String [] message = hmTicket.get(ticket).getMessage().split("\n");

			String expectedNumber = "UL";
			expectedNumber += message[5].substring(message[5].length()-3);
			
			assertEquals(expectedNumber, hmTicket.get(ticket).getSender());
		}
	}

	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#getMessageOut()}.
	 */
	public void testGetMessageOut() {
		String expectedMessage;
		for(int i=0;i<okTickets.length;i++)
		{
			String ticket = okTickets[i];
			Bundle data = hmData.get(ticket);
			try {
				hmTicket.get(ticket).create(data);
			} catch (TicketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			expectedMessage = data.getBoolean("price_reduced")?"UU":"UV";
			assertEquals(expectedMessage, hmTicket.get(ticket).getMessageOut());
		}
	}

	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#getNumberOut()}.
	 */
	public void testGetNumberOut() {
		String expectedNumber = "0704202222";
		for(int i=0;i<okTickets.length;i++)
		{
			String ticket = okTickets[i];
			assertEquals(expectedNumber, hmTicket.get(ticket).getNumberOut());
		}
	}

}
