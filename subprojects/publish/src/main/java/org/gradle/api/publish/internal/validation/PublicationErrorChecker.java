/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.publish.internal.validation;

import org.gradle.api.artifacts.PublishException;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.Category;
import org.gradle.api.internal.DocumentationRegistry;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;

import java.util.Optional;

/**
 * Static util class containing publication checks agnostic of the publication type.
 */
public abstract class PublicationErrorChecker {
    /**
     * Checks that the given component does not have any attributes that are not allowed to be published.
     *
     * @param component the component to check
     * @param documentationRegistry for creating helpful links in error messages upon failing the check
     * @throws PublishException if the component uses attributes invalid for publication
     */
    public static void checkForUnpublishableAttributes(SoftwareComponentInternal component, DocumentationRegistry documentationRegistry) {
        for (final UsageContext usageContext : component.getUsages()) {
            Optional<Attribute<?>> category = usageContext.getAttributes().keySet().stream()
                .filter(a -> Category.CATEGORY_ATTRIBUTE.getName().equals(a.getName()))
                .findFirst();

            category.ifPresent(c -> {
                Object value = usageContext.getAttributes().getAttribute(c);
                if (Category.VERIFICATION.equals(value)) {
                    throw new PublishException("Cannot publish module metadata for component '" + component.getName() + "' which would include a variant '" + usageContext.getName() + "' that contains a '" + Category.CATEGORY_ATTRIBUTE.getName() + "' attribute with a value of '" + Category.VERIFICATION + "'.  This attribute is reserved for test verification output and is not publishable.  See: " + documentationRegistry.getDocumentationFor("variant_attributes.html", "sec:verification_category"));
                }
            });
        }
    }
}
