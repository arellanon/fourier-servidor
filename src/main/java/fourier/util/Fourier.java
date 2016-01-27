package fourier.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fourier.dao.CancionDAO;
import fourier.dao.HuellaDigitalDAO;
import fourier.vo.CancionVO;
import fourier.vo.HuellaDigitalVO;

public class Fourier {

	double highscores[][];
	long points[][];
	Map<Integer, Map<Integer, Integer>> matchMap; // Map<SongId, Map<Offset, Count>>
	
	public Fourier(){
		this.matchMap = new HashMap<Integer, Map<Integer, Integer>>();
	}
	
	public ArrayList<HuellaDigitalVO> leer_query(JsonElement datos) {
		
		ArrayList<HuellaDigitalVO> listHuellaDigital = new ArrayList<HuellaDigitalVO>();
				        
		if (datos.isJsonArray()) {
	        JsonArray array = datos.getAsJsonArray();
	        java.util.Iterator<JsonElement> iter = array.iterator();
	        while(iter.hasNext()){
	        	HuellaDigitalVO huellaDigital = new HuellaDigitalVO();
				
	        	JsonElement entrada = iter.next();
	        	JsonObject obj = entrada.getAsJsonObject();
	        	huellaDigital.setHash(obj.get("hash").getAsLong());
				huellaDigital.setIdCancion(obj.get("idCancion").getAsInt());
				huellaDigital.setTiempo(obj.get("tiempo").getAsInt());
				listHuellaDigital.add(huellaDigital);
	        }
		}
		return listHuellaDigital;
	}
	
	public CancionVO buscar(ArrayList<HuellaDigitalVO> listQuery){

		ArrayList<CancionVO> canciones = new ArrayList<CancionVO>();
		CancionVO mejorCancion = new CancionVO();
		mejorCancion.setIdCancion(0);
		mejorCancion.setNombre("Vacio");
		mejorCancion.setUrl("");
		CancionDAO c = new CancionDAO();
		HuellaDigitalDAO huellaDigitalDAO = new HuellaDigitalDAO();
		ArrayList<HuellaDigitalVO> huellasDigitales = new ArrayList<HuellaDigitalVO>();
		int maxCont = 0;
		/*
		FileWriter fout = null;
		try {
			fout = new FileWriter("out.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
				
		//crear matchMap(resultado); //Map<IdCancion, Map<Offset, Cont>>
		for(HuellaDigitalVO query : listQuery) {
			//Cuento la cantidad de hash que coinciden con la BD
			if ((huellasDigitales = huellaDigitalDAO.consultarHuellaDigital( query.getHash() )) != null) {
				for (HuellaDigitalVO hD : huellasDigitales) {
					//valor absoluto del offset para saber en que parte del tema estamos
					int offset = Math.abs(hD.getTiempo() - query.getTiempo() );
					Map<Integer, Integer> tmpMap = null;
					//si no existe IdCancion en el matchMap
					if ((tmpMap = this.matchMap.get( hD.getIdCancion() )) == null) {
						tmpMap = new HashMap<Integer, Integer>();  //<offset, count>
						tmpMap.put(offset, 1);
						matchMap.put(hD.getIdCancion(), tmpMap);
					} else {
						//cuanto mayor sea el contador del offset, mas tramas consecutivos tenemos
						Integer count = tmpMap.get(offset);
						if (count == null) {
							tmpMap.put(offset, new Integer(1));
						} else {
							tmpMap.put(offset, new Integer(count + 1));
						}
					}
				}
			}
		}

		canciones = c.ListarCanciones();

		
		//Recorremos todas las canciones
		for( CancionVO cancion : canciones){
			System.out.println(cancion.toString());
			Map<Integer, Integer> tmpMap; // Map<Offset, Cont>
			if ( ( tmpMap = matchMap.get(cancion.getIdCancion() )) != null ) {
				//Si la cancion existe en matchMap
				//Almacenamos el mejor Cont de la cancion
				int maxContCancion = 0;
				for (Map.Entry<Integer, Integer> entry : tmpMap.entrySet() ) {
					if (entry.getValue() > maxContCancion) {
						maxContCancion = entry.getValue();
					}
					//System.out.println("CancionId = " + cancion.getIdCancion() + ", offset = " + entry.getKey() + ", Cantidad = " + entry.getValue());
				}
				//Almacenamos el mejor Cont de todas las canciones
				if (maxContCancion > maxCont) {
					maxCont = maxContCancion;
					mejorCancion = cancion;
				}
			}
		}
		String resultado = "Resultado: " + mejorCancion.toString();
		
		//Si el resultado es menor del umbral, no es suficiente
		if(maxCont < 10 ) {
			mejorCancion.setIdCancion(0);
			mejorCancion.setNombre("No hubo resultado para la busqueda");
			mejorCancion.setUrl(null);
		}
		System.out.println(resultado );
		System.out.println("Resultado: "+maxCont);

		return mejorCancion;
	}
	
}
