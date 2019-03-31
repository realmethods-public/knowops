#header()
package ${aib.getRootPackageName(true)}.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

/**
 * Spark transformer for transforming objects to Json objects using Google Gson
 * 
 * @author realMethods, Inc.
 *
 */
public class JsonTransformer implements ResponseTransformer {

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }

    // attributes
    private Gson gson = new Gson();

}
