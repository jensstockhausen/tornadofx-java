package tornadofx;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SuppressWarnings("unchecked")
public interface JsonModel {
	/**
	 * Fetch JSON values and update the model properties
	 * @param json The json to extract values from
	 */
	void updateModel(JsonObject json);

	/**
	 * Build a JSON representation of the model properties
	 * @param builder A builder that should be filled with the model properties
	 */
	void toJSON(JsonObjectBuilder builder);

	/**
	 * Copy all properties from this object to the given target object by converting to JSON and then updating the target.
	 * @param target The target object to update with the properties of this model
	 */
	default void copy(JsonModel target) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		toJSON(builder);
		target.updateModel(builder.build());
	}

	/**
	 * Copy all properties from the given source object to this object by converting to JSON and then updating this object.
	 * @param source The source object to extract properties from
	 */
	default void update(JsonModel source) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		source.toJSON(builder);
		updateModel(builder.build());
	}

	/**
	 * Duplicate this model object by creating a new object of the same type and copy over all the model properties.
	 *
	 * @param <T> The type of object
	 * @return A new object of type T with the model properties of this object
	 */
	default <T extends JsonModel> T copy() {
		try {
			T clone = (T) getClass().newInstance();
			JsonObjectBuilder builder = Json.createObjectBuilder();
			toJSON(builder);
			clone.updateModel(builder.build());
			return clone;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	default double getDouble(JsonObject json, String key, double defaultValue) {
		return json.containsKey(key) ? json.getJsonNumber(key).doubleValue() : defaultValue;
	}

	default long getLong(JsonObject json, String key, long defaultValue) {
		return json.containsKey(key) ? json.getJsonNumber(key).longValue() : defaultValue;
	}

	default LocalDate getLocalDate(JsonObject json, String key, LocalDate defaultValue) {
		return json.containsKey(key) ? LocalDate.parse(json.getString(key)) : defaultValue;
	}


}
