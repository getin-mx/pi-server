package mobi.allshoppings.cinepolis.services;

import java.util.HashMap;

import org.springframework.util.StringUtils;

public class CinepolisFormatMapper {

	public static final HashMap<String, String> map = new HashMap<String, String>();
	
	static {
		map.put("".toUpperCase(), 				"2D"	);
		map.put("Dig Esp".toUpperCase(), 		"2D"	);
		map.put("Dig Sub".toUpperCase(), 		"2D"	);
		map.put("Esp".toUpperCase(), 			"2D"	);
		map.put("Sub".toUpperCase(), 			"2D"	);
		map.put("4D/2D Esp".toUpperCase(), 		"4DX2D"	);
		map.put("4D/2D Sub".toUpperCase(), 		"4DX2D"	);
		map.put("SJ Esp".toUpperCase(), 		"2D"	);
		map.put("SJ Sub".toUpperCase(), 		"2D"	);
		map.put("XE Esp".toUpperCase(), 		"2D"	);
		map.put("XE Sub".toUpperCase(), 		"2D"	);
		map.put("XE 3D Esp".toUpperCase(), 		"3D"	);
		map.put("XE 3D Sub".toUpperCase(), 		"3D"	);
		map.put("3D Esp".toUpperCase(), 		"3D"	);
		map.put("3D Sub".toUpperCase(), 		"3D"	);
		map.put("4DX Esp".toUpperCase(), 		"4DX3D"	);
		map.put("4DX Sub".toUpperCase(), 		"4DX3D"	);
		map.put("IMAX Esp".toUpperCase(), 		"IMAX2D");
		map.put("IMAX Sub".toUpperCase(), 		"IMAX2D");
		map.put("IMAX 3D Esp".toUpperCase(), 	"IMAX3D");
		map.put("IMAX 3D Sub".toUpperCase(), 	"IMAX3D");
	}
	
	public static String map(String source) {
		if( StringUtils.hasText(source))
			return map.get(source.toUpperCase());
		else 
			return null;
	}
}
