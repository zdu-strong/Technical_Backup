package com.john.project.common.StorageResource;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Iterator;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import static eu.ciechanowiec.sneakyfun.SneakyToLongFunction.sneaky;

/**
 * A resource which is the logical concatenation of other resources.
 */
public class SequenceResource extends AbstractResource {

    /**
     * The resources to be concatenated.
     */
    private final Iterable<Resource> resources;
    private final String fileName;

    /**
     * Creates a new SequenceResource as the logical concatenation of the given
     * resources. Each resource is concatenated in iteration order as needed when
     * reading from the input stream of the SequenceResource. The mimetype of the
     * resulting concatenation is derived from the first resource.
     *
     * @param resources The resources to concatenate within the InputStream of this
     *                  SequenceResource.
     */
    public SequenceResource(String fileName, Iterable<Resource> resources) {
        super();
        this.resources = resources;
        this.fileName = fileName;
    }

    @Override
    public String getFilename() {
        return this.fileName;
    }

    @Override
    public InputStream getInputStream() {
        return new SequenceInputStream(new Enumeration<InputStream>() {

            /**
             * Iterator over all resources associated with this SequenceResource.
             */
            private final Iterator<Resource> resourceIterator = resources.iterator();

            @Override
            public boolean hasMoreElements() {
                return resourceIterator.hasNext();
            }

            @Override
            @SneakyThrows
            public InputStream nextElement() {
                return resourceIterator.next().getInputStream();
            }

        });
    }

    @Override
    public long contentLength() {
        return Lists.newArrayList(resources).stream().mapToLong(sneaky((s) -> s.contentLength())).sum();
    }

    @Override
    public String getDescription() {
        return null;
    }

}
