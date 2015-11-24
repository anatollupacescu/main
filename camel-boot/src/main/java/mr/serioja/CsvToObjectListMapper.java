package mr.serioja;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anatolie.lupacescu on 24/11/2015.
 */
public class CsvToObjectListMapper {

    public static <T> List<T> mapToObjects(String filePath, Map<String, Class<?>> typeMap, Class<T> clazz) throws Exception {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        List<T> resultList = new ArrayList<T>();
        File csvFile = new File(filePath);

        MappingIterator<String[]> reader = mapper.reader(String[].class).readValues(csvFile);

        final String[] headerLine = reader.next();

        String[] nextLine;
        while (reader.hasNext()) {
            nextLine = reader.next();

            Map<String, Object> map = Maps.newHashMap();

            for (int i = 0; i < headerLine.length; i++) {
                String headerName = headerLine[i];
                String value = nextLine[i];
                map.put(headerName, mapValue(typeMap, headerName, value));
            }

            T bean = mapper.convertValue(map, clazz);
            resultList.add(bean);
        }
        reader.close();
        return resultList;
    }

    private static Object mapValue(Map<String, Class<?>> typeMap, String headerName, String value) {
        Class<?> clazz = typeMap.get(headerName);
        if (Integer.class.equals(clazz)) {
            Object result = null;
            try {
                result = Integer.valueOf(value);
            } catch (Exception e) {
                throw new IllegalArgumentException("Could not cast to integer: " + value);
            }
            return result;
        } else if (String.class.equals(clazz)) {
            return value;
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }
}
