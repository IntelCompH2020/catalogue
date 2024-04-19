/*
 * Copyright 2021-2024 OpenAIRE AMKE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.catalogue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

public class ReflectUtils {

    private static final Logger logger = LogManager.getLogger(ReflectUtils.class);

    public static void setId(@NotNull Class<?> clazz, @NotNull Object resource, @NotNull String id) {
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(resource, id);
        } catch (NoSuchFieldException e) {
            logger.error("Could not find 'id' field in class [" + clazz.getName() + "]");
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
    }

    public static String getId(@NotNull Class<?> clazz, @NotNull Object resource) throws NoSuchFieldException {
        String id = null;
        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            id = (String) idField.get(resource);
        } catch (NoSuchFieldException e) {
            logger.error("Could not find 'id' field in class [" + clazz.getName() + "]");
            throw e;
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
        return id;
    }

    private ReflectUtils() {}
}
