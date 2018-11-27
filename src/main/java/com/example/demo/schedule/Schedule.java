package com.example.demo.schedule;

import java.util.ArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.demo.objects.ImpresionInfo;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.util.JposPropertiesConst;

@Component
public class Schedule {    
    public static ArrayList<ImpresionInfo> colaImpresion = new ArrayList<>();
    private POSPrinter ptr;
    
    private int findClosestElement(String contenido) {
		int[] simbols = new int[11];
		ArrayList<Integer> finalSimbols = new ArrayList<>();
		///order of elements 
		/* 0 = bc
		 * 1 = ca
		 * 2 = ra
		 * 3 = 5uc
		 * 4 = 2c
		 * 5 = 3c
		 * 6 = 4c
		 * 7 = 3hc
		 * 8 = 3vc
		 * 9 = 1uc
		 * 10 = N
		 */
		simbols[0] = contenido.indexOf("bC");	
		simbols[1] = contenido.indexOf("cA");	
		simbols[2] = contenido.indexOf("rA");	
		simbols[3] = contenido.indexOf("5uC");	
		simbols[4] = contenido.indexOf("2C");	
		simbols[5] = contenido.indexOf("3C");	
		simbols[6] = contenido.indexOf("4C");	
		simbols[7] = contenido.indexOf("3hC");	
		simbols[8] = contenido.indexOf("3vC");	
		simbols[9] = contenido.indexOf("1uC");
		simbols[10] = contenido.indexOf("N");	
		
		for(int  i = 0; i < simbols.length; i++) {
			if(simbols[i] > -1) {
				finalSimbols.add(simbols[i]);
			}
		}
		
		int minAt = 0;

		for (int i = 1; i < finalSimbols.size(); i++) {
				minAt = finalSimbols.get(i) < finalSimbols.get(minAt) ? i : minAt;
		}
		if(finalSimbols.size() > 0) {
			System.out.println("CLOSEST ELEMENT: "+ finalSimbols.get(minAt));
			return finalSimbols.get(minAt);
		}else {
			return -1;
		}
    }
    
	@Scheduled(fixedRate = 3000)
	public void scheduled() {
		System.out.println("Comienza busqueda de elementos en cola");
		if(!colaImpresion.isEmpty()) {
			boolean isFoundNothing = false;
			int currentIndex = 0;
			int beforeSimbolIndex = 0;
			String contenido = colaImpresion.get(0).getMessage(); //mucho contenido rA por aqui rA
			byte ESCSquence[] = new byte[]{0x1B, 0x7C};
			if(!contenido.contains(new String(ESCSquence))) {
				while(!isFoundNothing) {
					int closestElement = findClosestElement(contenido.substring(currentIndex, contenido.length()));
					if(closestElement > -1) {
						beforeSimbolIndex = currentIndex + closestElement;
					/*	if(contenido.substring(currentIndex, contenido.length()).contains("bC")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("bC");
						}else if(contenido.substring(currentIndex, contenido.length()).contains("cA")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("cA"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("rA") ){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("rA"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("5uC") ){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("5uC"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("2C")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("2C"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("3C") ){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("3C"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("4C") ){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("4C"); 
						}*//*else if(contenido.substring(currentIndex, contenido.length()).contains("3hC")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("3hC"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("3vC")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("3vC"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("1uC")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("1uC"); 
						}else if(contenido.substring(currentIndex, contenido.length()).contains("N")){
							beforeSimbolIndex = currentIndex + contenido.substring(currentIndex, contenido.length()).indexOf("N"); 
						}*///else {
						//	isFoundNothing = true; 
							
						//	return;
					//	}	
					}else {
						isFoundNothing = true;
						colaImpresion.get(0).setMessage(contenido);
						System.out.println(contenido);
						return;
					}
					String primerMitad = contenido.substring(currentIndex, beforeSimbolIndex) + new String(ESCSquence);
					String segundaMitad = contenido.substring(beforeSimbolIndex, contenido.length()); 
					contenido = contenido.substring(0, currentIndex) + primerMitad + segundaMitad;
					currentIndex = beforeSimbolIndex +4; 
				}
			}
			try {	
				ptr = new POSPrinter();
				System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "src/main/java/jpos.xml");
				String logicalName = "SRP-350plusIII";
				ptr.open(logicalName);
				ptr.claim(1000);
				ptr.setDeviceEnabled(true);
				
				String imp = colaImpresion.get(0).getMessage();
				System.out.println(imp);
				ptr.printNormal(POSPrinterConst.PTR_S_RECEIPT, imp);
				
				//ptr.printNormal(POSPrinterConst.PTR_S_RECEIPT, new String(ESCSquence) + "7lF");
				//ptr.cutPaper(10);
				System.out.println("ticket "+ colaImpresion.get(0).getCount()+" impreso");
				colaImpresion.remove(0);
			}catch(JposException e) {
				System.out.println(e.getMessage());
			}finally {
				try {
					ptr.setDeviceEnabled(false);
					ptr.release();
					ptr.close();
				}catch(JposException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
