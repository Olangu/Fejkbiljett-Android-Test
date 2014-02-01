package com.fejkbiljett.android.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.os.Bundle;

import com.fejkbiljett.android.Utils;
import com.fejkbiljett.android.tickets.StockholmTicket;
import com.fejkbiljett.android.tickets.Ticket;
import com.fejkbiljett.android.tickets.Ticket.TicketException;

import junit.framework.TestCase;

public class StockholmTicketTest extends TestCase {

	HashMap<String,Ticket> hmTicket = new HashMap<String,Ticket>();
	HashMap<String,Bundle> hmData = new HashMap<String,Bundle>();
	
	String[] okTickets = { "full_a", "full_b", "full_c",
						   "reduced_a", "reduced_b", "reduced_c",
						   "full_ab", "full_bc", "full_abc",
						   "reduced_ab", "reduced_bc", "reduced_abc",
						   "full_cl", "full_bcl", "full_abcl",
						   "reduced_cl", "reduced_bcl", "reduced_abcl" };
	
	String[] nokTickets = { "full_ac", "full_al", "full_bl",
							"reduced_ac", "reduced_al", "reduced_bl" };

	
	public StockholmTicketTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		for (int i=0; i<okTickets.length; i++) {
			String ticket = okTickets[i];
					
			hmTicket.put(ticket, new StockholmTicket());
			hmData.put(ticket, new Bundle());
			
			Bundle data = hmData.get(ticket);
			
			//Is price reduced?
			data.putBoolean("price_reduced", ticket.contains("reduced"));

			//Set zones
			String ticketParts[] = ticket.split("_");
			data.putBoolean("zone_a", ticketParts[1].contains("a"));
			data.putBoolean("zone_b", ticketParts[1].contains("b"));
			data.putBoolean("zone_c", ticketParts[1].contains("c"));
			data.putBoolean("zone_l", ticketParts[1].contains("l"));
		}
		for (int i=0; i<nokTickets.length; i++) {
			String ticket = nokTickets[i];
			
			hmTicket.put(ticket, new StockholmTicket());
			hmData.put(ticket, new Bundle());
			
			Bundle data = hmData.get(ticket);
			
			//Is price reduced?
			data.putBoolean("price_reduced", ticket.contains("reduced"));

			//Set zones
			String ticketParts[] = ticket.split("_");
			data.putBoolean("zone_a", ticketParts[1].contains("a"));
			data.putBoolean("zone_b", ticketParts[1].contains("b"));
			data.putBoolean("zone_c", ticketParts[1].contains("c"));
			data.putBoolean("zone_l", ticketParts[1].contains("l"));			
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
		
		for(int i=0;i<nokTickets.length;i++)
		{
			String ticket = nokTickets[i];
			Boolean createSuccess=true;
			try {
				hmTicket.get(ticket).create(
						hmData.get(ticket) );
			} catch (TicketException e) {
				createSuccess=false;
			}
			assertFalse("Ticket (" + ticket +") created.", createSuccess);
		}
	}

	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#getMessage()}.
	 */
	public void testGetMessage() {
		int [] prices = { 36, 54, 72, 36 };
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
			
			cal.add(Calendar.MINUTE, ( (data.getBoolean("zone_a") && 
										data.getBoolean("zone_b") && 
										data.getBoolean("zone_c")) ||
										data.getBoolean("zone_l") ? 120 : 75 ));
			String validTime = new SimpleDateFormat("HH:mm").format(cal.getTime());
			String validDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

			String [] message = hmTicket.get(ticket).getMessage().split("\n");
			
			long code = Long.parseLong(message[9].replaceAll(" ", ""));
			
			String zoneString = data.getBoolean("zone_a")?"A":"";
			zoneString += data.getBoolean("zone_b")?"B":"";
			zoneString += data.getBoolean("zone_c")?"C":"";
			zoneString += data.getBoolean("zone_l")?"L":"";
			
			expectedString = data.getBoolean("price_reduced")?"R-":"H-";
			expectedString += zoneString;
			expectedString += " " + validTime + " " + message[9].substring(message[9].length()-3);
			
			assertEquals(expectedString,message[0]);
			
			assertEquals("",message[1]);
			assertEquals('E',message[2].charAt(0));
			assertEquals('E',message[3].charAt(0));
			assertEquals('E',message[4].charAt(0));
			assertEquals("EEEEEEEEEE",message[5]);
			assertEquals("", message[6]);
			
			String codeStr = message[2].substring(1) + 
							 message[3].substring(1) + 
							 message[4].substring(1, message[4].length()-3);
			
			assertEquals(code, Long.parseLong(Utils.aeoxToHex(codeStr),16));
			
			expectedString = "SL biljett giltig till " + validTime + ", " + validDate;
			assertEquals(expectedString, message[7]);
			
			int price;
			if(data.getBoolean("zone_l")) {
				price = prices[zoneString.length()-2] + prices[prices.length-1];
			} else {
				price = prices[zoneString.length()-1];
			}
			if(data.getBoolean("price_reduced")) {
				price = (int) Math.ceil(price*0.55);
			}
				
			expectedString = data.getBoolean("price_reduced")?"Red pris ":"Helt pris ";
			expectedString += price;
			expectedString += " kr ink 6% moms";
						
			assertEquals(expectedString, message[8]);
			assertEquals("m.sl.se", message[10]);
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

			String expectedNumber = "SL";
			expectedNumber += message[9].substring(message[9].length()-3);
			
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
			expectedMessage = data.getBoolean("price_reduced")?"R":"H";
			expectedMessage += data.getBoolean("zone_a")?"A":"";
			expectedMessage += data.getBoolean("zone_b")?"B":"";
			expectedMessage += data.getBoolean("zone_c")?"C":"";
			expectedMessage += data.getBoolean("zone_l")?"L":"";
			assertEquals(expectedMessage, hmTicket.get(ticket).getMessageOut());
		}
	}

	/**
	 * Test method for {@link com.fejkbiljett.android.tickets.StockholmTicket#getNumberOut()}.
	 */
	public void testGetNumberOut() {
		String expectedNumber = "0767201010";
		for(int i=0;i<okTickets.length;i++)
		{
			String ticket = okTickets[i];
			assertEquals(expectedNumber, hmTicket.get(ticket).getNumberOut());
		}
	}

}
