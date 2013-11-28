package com.example.barcode;

import java.util.HashMap;
import java.util.Map;

import com.JohnnyWorks.videoNpix.R;

public class GoodsItem {
	private String barcode;
	private String name;
	private String price;
	private String detail;
	public static final String ID = "_id";
	public static final String BARCODE = "barcode";
	public static final String NAME = "name";
	public static final String PRICE = "price";
	public static final String DETAIL = "detail";
	private final static String TABLE_NAME = "products";
	public  Map<String, Integer> bmap=new HashMap<String, Integer>();
	public GoodsItem setGoods(String bbarcode,String nname,String pprice,String ddetail){
		barcode=bbarcode;
		name=nname;
		price=pprice;
		detail=ddetail;
		return this;
	}
	public String toString() {
		String ret="";
		ret="INSERT INTO " + TABLE_NAME + "(" + ID + ", "+ BARCODE + ", " + NAME + ", " + PRICE + ", "+ DETAIL + ") VALUES " 
				+"(null,"+"\""+barcode+"\""+","+"\""+name+"\""+","+"\""+price+"\""+","+"\""+detail+"\""+")";
		return ret;
	}
	public GoodsItem() {
		super();
		bmap.put("img9787111267768", R.drawable.img9787111267768);
		bmap.put("img9787111276869", R.drawable.img9787111276869);
		bmap.put("img9787111323570", R.drawable.img9787111323570);
		bmap.put("img9787111337270", R.drawable.img9787111337270);
		bmap.put("img9787111347446", R.drawable.img9787111347446);
		bmap.put("img9787111357629", R.drawable.img9787111357629);
		bmap.put("img9787115156655", R.drawable.img9787115156655);
		bmap.put("img9787115298027", R.drawable.img9787115298027);
		bmap.put("img9787121035753", R.drawable.img9787121035753);
		bmap.put("img9787121100000", R.drawable.img9787121100000);
		bmap.put("img9787302059998", R.drawable.img9787302059998);
		bmap.put("img9787302177869", R.drawable.img9787302177869);
		bmap.put("img9787508338637", R.drawable.img9787508338637);
		bmap.put("img9787508356464", R.drawable.img9787508356464);
		bmap.put("img9787564115203", R.drawable.img9787564115203);
		bmap.put("img9789574428618", R.drawable.img9789574428618);	
	}
	public int getR(String barcode){
		System.out.println("aa"+barcode);
		if(barcode.equals("")||barcode.length()!=13)return -1;
		String a="img"+barcode;
		int ret=0;
		if(bmap.get(a)!=null)
	    ret=bmap.get(a);	
		System.out.println(ret+"bbbb");
		return ret;
	}
	
}
