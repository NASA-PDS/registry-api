package gov.nasa.pds.api.registry.business;

/**
 * Used by API calls, such as "/bundles/{lidvid}/collections", 
 * "/bundles/{lidvid}/collections/latest", 
 * "/bundles/{lidvid}/collections/all", etc., 
 * to select product versions.
 * 
 * @author karpenko
 */
public enum ProductVersionSelector
{
    /**
     * Original version of a product, e.g., from LIDVID reference field
     */
    ORIGINAL,
    /**
     * All versions of a product
     */
    ALL, 
    /**
     * Latest version of a product
     */
    LATEST
}
