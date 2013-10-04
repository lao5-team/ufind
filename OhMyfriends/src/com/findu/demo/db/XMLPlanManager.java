package com.findu.demo.db;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class XMLPlanManager {
	public static String TAG = XMLPlanManager.class.getName();
	private ArrayList<Plan> mPlans = new ArrayList<Plan>();
	private static XMLPlanManager instance = null;
	
	public static XMLPlanManager getInstance()
	{
		if(null == instance)
		{
			instance = new XMLPlanManager();
		}
		return instance;
	}
	
	public ArrayList<Plan> getPlans()
	{
		return mPlans;
	}
	
	public void setPlans(ArrayList<Plan> plans)
	{
		mPlans = plans;
		writePlansToXML("mnt/sdcard/test.xml");
	}
	
	private XMLPlanManager()
	{
		readPlansFromXML("mnt/sdcard/test.xml");
	}
	
	private void readPlansFromXML(String fileName)
	{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try 
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			FileInputStream fis = new FileInputStream(fileName);
			Document dom = builder.parse(fis);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("plan");//查找所有person节点
			for (int i = 0; i < items.getLength(); i++)
			{
				Plan plan = new Plan();
				//得到第一个person节点
				Element personNode = (Element) items.item(i);
				//获取person节点下的所有子节点(标签之间的空白节点和name/age元素)
				NodeList childsNodes = personNode.getChildNodes();
				for (int j = 0; j < childsNodes.getLength(); j++) 
				{
					Node node = (Node) childsNodes.item(j); //判断是否为元素类型
					if(node.getNodeType() == Node.ELEMENT_NODE)
					{    
						Element childNode = (Element) node;
					    //判断是否name元素
					    if ("name".equals(childNode.getNodeName())) 
					    {
					    	//获取name元素下Text节点,然后从Text节点获取数据
					    	//person.setName(childNode.getFirstChild().getNodeValue());
					    	plan.name = childNode.getFirstChild().getNodeValue();
					    } 
					    if ("destLat".equals(childNode.getNodeName())) 
					    {
					    	//获取name元素下Text节点,然后从Text节点获取数据
					    	plan.destLatitude = Integer.parseInt(childNode.getFirstChild().getNodeValue());
					    } 
					    if ("destLong".equals(childNode.getNodeName())) 
					    {
					    	plan.destLatitude = Integer.parseInt(childNode.getFirstChild().getNodeValue());
					    }
					    if ("startTime".equals(childNode.getNodeName())) 
					    {
					    	//获取name元素下Text节点,然后从Text节点获取数据
					    	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
					    	plan.startTime = formatter.parse(childNode.getFirstChild().getNodeValue());
					    } 
					    if ("isDayly".equals(childNode.getNodeName())) 
					    {
					    	//获取name元素下Text节点,然后从Text节点获取数据
					    	plan.isDaylyRemind = Boolean.parseBoolean(childNode.getFirstChild().getNodeValue());
					    	//person.setName(childNode.getFirstChild().getNodeValue());                              
					    } 
					    if ("duration".equals(childNode.getNodeName())) 
					    {
					    	//获取name元素下Text节点,然后从Text节点获取数据
					    	plan.duration = Integer.parseInt(childNode.getFirstChild().getNodeValue());
					    } 
					}
			   }
			   mPlans.add(plan);
			}
			fis.close();
			Log.d(TAG, "count" + mPlans.size());
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	private void writePlansToXML(String fileName)
	{
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(fos, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "plans");
			for (Plan p : mPlans) {
				serializer.startTag(null, "plan");
				serializer.startTag(null, "name");
				serializer.text(p.name);
				serializer.endTag(null, "name");
				serializer.startTag(null, "destLat");
				serializer.text(p.destLatitude + "");
				serializer.endTag(null, "destLat");
				serializer.startTag(null, "destLong");
				serializer.text(p.destLongitude + "");
				serializer.endTag(null, "destLong");
				serializer.startTag(null, "startTime");
				serializer.text(p.startTime.toString());
				serializer.endTag(null, "startTime");
				serializer.startTag(null, "isDayly");
				serializer.text(p.isDaylyRemind + "");
				serializer.endTag(null, "isDayly");
				serializer.startTag(null, "isDayly");
				serializer.text(p.duration + "");
				serializer.endTag(null, "isDayly");
				serializer.endTag(null, "plan");
			}
			serializer.endTag(null, "plans");
			serializer.endDocument();
			// fos.flush();
			// fos.close();
		}
		catch(Exception e)
		{
			
		}
	}
}
