//package com.fjd.query;
//
//import com.fjd.exception.BusinessException;
//import org.apache.commons.lang3.StringUtils;
//import org.dozer.DozerBeanMapper;
//import org.dozer.util.DozerConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class QueryUtils {
//    private static final Logger LOGGER = LoggerFactory.getLogger(QueryUtils.class);
//
//    public static final String QUERY_PARAM_FILTER = "filter";
//    public static final String QUERY_PARAM_EXPAND = "expand";
//    public static final String QUERY_PARAM_SELECT = "select";
//    public static final String QUERY_PARAM_ORDERBY = "orderby";
//    public static final String QUERY_PARAM_LIMIT = "limit";
//    public static final String QUERY_PARAM_OFFSET = "offset";
//
//    public static final int DEFAULT_PARAM_LIMIT = 100;
//    public static final int DEFAULT_PARAM_OFFSET = 0;
//
//    public static final String QUERY_FIELD_SEPERATOR = ",";
//    public static final String QUERY_FIELD_WILDCARD = "*";
//
//    private static Map<String, String> props = new HashMap<>();
//    static {
//        props.put(SearchUtils.DATE_FORMAT_PROPERTY, APIUtils.ISO8601_DATE_FORMAT_STR);
//    }
//
//    private QueryUtils() {
//    }
//
//    public static String buildBOQL(Map<String, Object> params, BusinessObjectFacade boFacade, MappingProcessor mappingProcessor,
//                                   Class<?> roClass, Class<?> boClass, String filter, String orderBy, boolean isCount) {
//
//    	LOGGER.debug("Generating BOQL for RO: {}, BO: {}, filter: {}, orderby: {}, isCount: {}", roClass.getName(), boClass.getName(), filter, orderBy, isCount);
//    	String boql = "";
//
//    	// get bo type & meta for later use
//    	BusinessObjectType boType = boFacade.getMetadataRepository().getBusinessObjectType(boClass);
//    	MetadataRepository metadataRepository = boFacade.getMetadataRepository();
//
//    	// parse the filter and order by
//    	BoolCommonExpression filterExp = StringUtils.isBlank(filter) ? null : (BoolCommonExpression) ExpressionParser.parse(filter, APIUtils.DEFAULT_FIELD_DELIMITER);
//    	List<OrderByExpression> orderByExps = StringUtils.isBlank(orderBy) ? null : ExpressionParser.parseOrderBy(orderBy, APIUtils.DEFAULT_FIELD_DELIMITER);
//    	QueryInfo qi = new QueryInfo(null, 0, 0, filterExp, orderByExps, null, null, null, Collections.emptySet(), null, null);
//
//    	// get the udf field info
//    	Map<String, BOBaseType> udfBaseTypeMap = QueryUtils.buildUDFBaseTypeMap(boType);
//
//    	APIBOQLCriteriaAdapter rocr = new APIBOQLCriteriaAdapter(qi, boType, metadataRepository, mappingProcessor, roClass, boClass, udfBaseTypeMap);
//    	boql = rocr.getBOQL(params, isCount);
//
//		LOGGER.debug("Generated BOQL is: {}", boql);
//		return boql;
//    }
//
//    public static <RO extends ResourceObject, BO extends BusinessObject> String query(DozerBeanMapper mapper,
//            Class<RO> roClass, Class<BO> boClass, BusinessObjectType boType, Map<String, Object> params, String filter,
//            String orderby) {
//
//        String boql = "";
//        Map<String, String> fieldMap = buildFieldMap(mapper, roClass, boClass, boType);
//
//        if (StringUtils.isEmpty(filter)) {
//            APIQueryVisitor<RO, BO> jpa = new APIQueryVisitor<>(roClass, boClass, boType, fieldMap, null, null, orderby);
//            boql = jpa.getQueryWithNoFilter();
//        } else {
//            params.clear();
//            filter = filterPreProcess(filter);
//            Map<String, BOBaseType> udfBaseTypeMap = buildUDFBaseTypeMap(boType);
//            try {
//                SearchCondition<RO> condition = new APIODataParser<>(roClass, props).parse(filter);
//                APIQueryVisitor<RO, BO> visitor = new APIQueryVisitor<>(roClass, boClass, boType, fieldMap,
//                        udfBaseTypeMap, null,
//                        orderby);
//                condition.accept(visitor);
//                params.putAll(visitor.getParamMap());
//                boql = visitor.getQuery();
//            } catch (Exception e) {
//                LOGGER.info("Error parsing or generating BOQL for filter: '{}'! Check log for detail. Caused by {}.",
//                        filter, e);
//                throw new BusinessException(e);
//            }
//        }
//        LOGGER.debug("Generated BOQL = {}", boql);
//        return boql;
//    }
//
//    public static <RO extends ResourceObject, BO extends BusinessObject> String count(DozerBeanMapper mapper,
//            Class<RO> roClass, Class<BO> boClass, BusinessObjectType boType, Map<String, Object> params, String filter) {
//
//        String boql = "";
//        Map<String, String> fieldMap = buildFieldMap(mapper, roClass, boClass, boType);
//
//        if (StringUtils.isEmpty(filter)) {
//            APIQueryVisitor<RO, BO> jpa = new APIQueryVisitor<>(roClass, boClass, boType, fieldMap, null);
//            boql = jpa.getCountQueryWithNoFilter();
//        } else {
//            params.clear();
//            filter = filterPreProcess(filter);
//            Map<String, BOBaseType> udfBaseTypeMap = buildUDFBaseTypeMap(boType);
//            try {
//                SearchCondition<RO> condition = new APIODataParser<>(roClass, props).parse(filter);
//                APIQueryVisitor<RO, BO> visitor = new APIQueryVisitor<>(roClass, boClass, boType, fieldMap,
//                        udfBaseTypeMap);
//                condition.accept(visitor);
//                params.putAll(visitor.getParamMap());
//                boql = visitor.getQuery();
//            } catch (Exception e) {
//                LOGGER.info("Error parsing or generating BOQL for filter: '{}'! Check log for detail. Caused by {}.",
//                        filter, e);
//                throw new BusinessException(e);
//            }
//        }
//        LOGGER.debug("Generated BOQL = {}", boql);
//        return boql;
//    }
//
//    private static <RO extends ResourceObject, BO extends BusinessObject> Map<String, String> buildFieldMap(
//            DozerBeanMapper mapper, Class<RO> roClass, Class<BO> boClass, BusinessObjectType boType) {
//        Map<String, String> result = new HashMap<>();
//        ClassMappingMetadata classMapping = mapper.getMappingMetadata().getClassMapping(roClass, boClass);
//        for (FieldMappingMetadata fieldMapping : classMapping.getFieldMappings()) {
//            result.put(fieldMapping.getSourceName(), fieldMapping.getDestinationName());
//        }
//        for (Property prop : boType.getRootNode().getUserFieldsMeta()) {
//            UserProperty udf = (UserProperty) prop;
//            String fullApiName = udf.getFullApiName();
//            String fullName = udf.getFullName();
//            if (MoneyRenderType.get(udf.getType()) != null) {
//                ComplexType ctype = TypeUtils.getComplexType(MoneyRenderType.getBaseType());
//                for (Element element : ctype.getElements()) {
//                    result.put(fullApiName + "." + element.getName(), fullName + "_" + element.getName());
//                }
//            } else {
//                result.put(udf.getFullApiName(), udf.getFullName());
//            }
//        }
//
//        return result;
//    }
//
//    public static Map<String, BOBaseType> buildUDFBaseTypeMap(BusinessObjectType boType) {
//        Map<String, BOBaseType> result = new HashMap<>();
//        for (Property prop : boType.getRootNode().getUserFieldsMeta()) {
//            UserProperty udf = (UserProperty) prop;
//            String fullApiName = udf.getFullApiName();
//            if (MoneyRenderType.get(udf.getType()) != null) {
//                ComplexType ctype = TypeUtils.getComplexType(MoneyRenderType.getBaseType());
//                for (Element element : ctype.getElements()) {
//                    result.put(fullApiName + "." + element.getName(), UDFUtils.getStorageType(element.getType()));
//                }
//            } else {
//                result.put(udf.getFullApiName(), UDFUtils.getStorageType(udf.getType()));
//            }
//        }
//        return result;
//    }
//
//    /**
//     * The key of map is "udf full name", not "udf full api name"
//     * @param nodeType
//     * @return
//     */
//    public static Map<String, BOBaseType> buildUDFBaseTypeMap(NodeType nodeType) {
//        Map<String, BOBaseType> result = new HashMap<>();
//        for (Property prop : nodeType.getUserFieldsMeta()) {
//            UserProperty udf = (UserProperty) prop;
//            String fullName = udf.getFullName();
//            if (MoneyRenderType.get(udf.getType()) != null) {
//                ComplexType ctype = TypeUtils.getComplexType(MoneyRenderType.getBaseType());
//                for (Element element : ctype.getElements()) {
//                    result.put(fullName + "_" + element.getName(), UDFUtils.getStorageType(element.getType()));
//                }
//            } else {
//                result.put(udf.getFullName(), UDFUtils.getStorageType(udf.getType()));
//            }
//        }
//        return result;
//    }
//
//    public static Map<String, String> buildUdfFieldMap(NodeType nodeType) {
//        Map<String, String> udfFieldMap = new HashMap<String, String>();
//        for (Property prop : nodeType.getUserFieldsMeta()) {
//            UserProperty udf = (UserProperty) prop;
//            String fullApiName = udf.getFullApiName();
//            String fullName = udf.getFullName();
//            if (MoneyRenderType.get(udf.getType()) != null) {
//                ComplexType ctype = TypeUtils.getComplexType(MoneyRenderType.getBaseType());
//                for (Element element : ctype.getElements()) {
//                    udfFieldMap.put(fullApiName + "." + element.getName(), fullName + "_" + element.getName());
//                }
//            } else {
//                udfFieldMap.put(udf.getFullApiName(), udf.getFullName());
//            }
//        }
//        return udfFieldMap;
//    }
//
//
//    public static String getUdfName(NodeType nodeType, String roFieldName) {
//        String simpleUdfName = APIUtils.getUdfSimpleName(roFieldName);
//        Map<String, String> udfFieldMap = buildUdfFieldMap(nodeType);
//        return udfFieldMap.get(simpleUdfName);
//    }
//
//    private static String filterPreProcess(String filter) {
//        LOGGER.info("Original filter string = '{}'", filter);
//        if (StringUtils.isEmpty(filter)) {
//            return "";
//        }
//        String[] words = filter.split(" ");
//        StringBuilder sb = new StringBuilder();
//        int count = 0;
//        for (String w : words) {
//            if (w.indexOf('\'') >= 0) {
//                count += StringUtils.countMatches(w, "'");
//                sb.append(w);
//                sb.append(" ");
//                continue;
//            }
//            if (w.matches("[0-9]+\\.[0-9]+\\)*") && count % 2 == 0) {
//                if (w.endsWith(")")) {
//                    w = w.replaceFirst("\\)", "d\\)");
//                } else {
//                    w += 'd';
//                }
//            }
//            sb.append(w);
//            sb.append(" ");
//        }
//        String result = sb.toString();
//        LOGGER.info("Converted filter string = '{}'", result);
//        return result;
//    }
//
//    // boFieldName has no "customFields" prefix
//    public static BOBaseType getUdfBOBaseType(String boFieldName, NodeType nodeType, Class<?> boClass) {
//
//        if (!APIUtils.containsUdf(boFieldName)) {
//            // it's just name like "ext_default_UDF1"
//            Map<String, BOBaseType> udfBOBaseTypeMap = buildUDFBaseTypeMap(nodeType);
//            return udfBOBaseTypeMap.get(boFieldName);
//        } else {
//            // it's the boFieldName like "productLines.customFields.ext_default_UDF1", so, it's a udf contained in subNode.
//            // split the original field name into the 1st part and rest part
//            String parentFieldName = APIUtils.get1stSegment(boFieldName);
//            String restFieldName = APIUtils.remove1stSegment(boFieldName);
//
//            // remove the "customFields" prefix
//            String simpleUdfName = APIUtils.getUdfSimpleName(restFieldName);
//            Class<?> subClass = APIUtils.getFieldType(boClass, parentFieldName);
//            NodeType subNodeType = nodeType.getBusinessObjectType().getNodeType(subClass.getSimpleName());
//            return getUdfBOBaseType(simpleUdfName, subNodeType, subClass);
//        }
//    }
//
//    public static String getDestPropertyName(MappingProcessor mappingProcessor, Class<?> srcClass, Class<?> destClass, String originalPropertyName, NodeType nodeType) {
//        if (!APIUtils.isValidField(originalPropertyName, srcClass)) {
//            throw new BusinessException(APIFrwErrorCode.API_FIELD_NOT_EXIST, originalPropertyName);
//
//        }
//        List<String> destPropNames = new ArrayList<String>();
//
//        buildDestPropertyName(mappingProcessor, srcClass, destClass, originalPropertyName, destPropNames, nodeType);
//        StringBuilder compositedDestPropName = new StringBuilder();
//        for (String propName : destPropNames) {
//            compositedDestPropName.append(propName).append(DozerConstants.DEEP_FIELD_DELIMITER);
//        }
//        String result = compositedDestPropName.toString();
//        result = result.substring(0, result.length() - 1);
//        return result;
//    }
//
//
//    private static void buildDestPropertyName(MappingProcessor mappingProcessor, Class<?> srcClass, Class<?> destClass, String srcPropertyName, List<String> destPropNames, NodeType nodeType) {
//        if (APIUtils.isUdf(srcPropertyName)) {
//            String udfName = getUdfName(nodeType, srcPropertyName);
//            destPropNames.add(udfName);
//            return;
//        }
//
//        // get the map from BO to RO!!! Yes!!! Since we're querying, and the actual useful mapping is from BO to RO.
//        ClassMap classMap = mappingProcessor.getClassMap(destClass, srcClass, null);
//
//        if (classMap == null) {
//            throw new IllegalArgumentException("Invalid input classes!");
//        }
//
//        String curPropertyName = srcPropertyName;
//        FieldMap fieldMap = classMap.getFieldMapUsingDest(curPropertyName);
//        String restPropertyName = "";
//        while (fieldMap == null && APIUtils.isCompositeProperty(curPropertyName)) {
//            restPropertyName = APIUtils.getLastSegment(curPropertyName).concat(DozerConstants.DEEP_FIELD_DELIMITER).concat(restPropertyName);
//            curPropertyName = APIUtils.removeLastSegment(curPropertyName);
//            fieldMap = classMap.getFieldMapUsingDest(curPropertyName);
//        }
//
//        if (fieldMap == null) {
//            // can't find field map, return original property name
//            throw new BusinessException(APIFrwErrorCode.API_QUERY_UNSUPPORTED_FIELD, srcPropertyName);
//        } else {
//            // check whether the mapped bo field exists on bo
//            Class<?> subDestClass = APIUtils.getFieldType(destClass, fieldMap.getSrcFieldName());
//            if (subDestClass == null) {
//                throw new BusinessException(APIFrwErrorCode.API_QUERY_UNSUPPORTED_FIELD, srcPropertyName);
//            }
//
//            // find the field map for some preceeding fields
//            destPropNames.add(fieldMap.getSrcFieldName());
//            if (StringUtils.isNotBlank(restPropertyName)) {
//                // remove the ending "." symbol
//                restPropertyName = APIUtils.removeLastSegment(restPropertyName);
//                Class<?> subSrcClass = APIUtils.getFieldType(srcClass, curPropertyName);
//                NodeType subNodeType = nodeType.getBusinessObjectType().getNodeType(subDestClass.getSimpleName());
//                buildDestPropertyName(mappingProcessor, subSrcClass, subDestClass, restPropertyName, destPropNames, subNodeType);
//            }
//            return;
//        }
//    }
//
//    public static String getDestPropertyName(MappingProcessor mappingProcessor, Class<?> srcClass, Class<?> destClass, String originalPropertyName, Map<String, String> udfFieldMap) {
//        if (!APIUtils.isValidField(originalPropertyName, srcClass)) {
//            throw new BusinessException(APIFrwErrorCode.API_FIELD_NOT_EXIST, originalPropertyName);
//
//        }
//    	List<String> destPropNames = new ArrayList<String>();
//
//    	buildDestPropertyName(mappingProcessor, srcClass, destClass, originalPropertyName, destPropNames, udfFieldMap);
//    	StringBuilder compositedDestPropName = new StringBuilder();
//    	for (String propName : destPropNames) {
//    		compositedDestPropName.append(propName).append(DozerConstants.DEEP_FIELD_DELIMITER);
//    	}
//    	String result = compositedDestPropName.toString();
//    	result = result.substring(0, result.length() - 1);
//    	return result;
//    }
//
//    private static void buildDestPropertyName(MappingProcessor mappingProcessor, Class<?> srcClass, Class<?> destClass, String srcPropertyName, List<String> destPropNames, Map<String, String> udfFieldMap) {
//    	if (APIUtils.isUdf(srcPropertyName)) {
//    		String simpleUdfName = APIUtils.getUdfSimpleName(srcPropertyName);
//    		destPropNames.add(udfFieldMap.get(simpleUdfName));
//    		return;
//    	}
//
//    	// get the map from BO to RO!!! Yes!!! Since we're querying, and the actual useful mapping is from BO to RO.
//        ClassMap classMap = mappingProcessor.getClassMap(destClass, srcClass, null);
//
//        if (classMap == null) {
//            throw new IllegalArgumentException("Invalid input classes!");
//        }
//
//        String curPropertyName = srcPropertyName;
//        FieldMap fieldMap = classMap.getFieldMapUsingDest(curPropertyName);
//        String restPropertyName = "";
//        while (fieldMap == null && APIUtils.isCompositeProperty(curPropertyName)) {
//            restPropertyName = APIUtils.getLastSegment(curPropertyName).concat(DozerConstants.DEEP_FIELD_DELIMITER).concat(restPropertyName);
//            curPropertyName = APIUtils.removeLastSegment(curPropertyName);
//            fieldMap = classMap.getFieldMapUsingDest(curPropertyName);
//        }
//
//        if (fieldMap == null) {
//            // can't find field map, return original property name
//            throw new BusinessException(APIFrwErrorCode.API_QUERY_UNSUPPORTED_FIELD, srcPropertyName);
//        } else {
//            // find the field map for some preceeding fields
//            destPropNames.add(fieldMap.getSrcFieldName());
//            if (StringUtils.isNotBlank(restPropertyName)) {
//                // remove the ending "." symbol
//                restPropertyName = APIUtils.removeLastSegment(restPropertyName);
//                Class<?> subSrcClass = APIUtils.getFieldType(srcClass, curPropertyName);
//                Class<?> subDestClass = APIUtils.getFieldType(destClass, fieldMap.getSrcFieldName());
//                buildDestPropertyName(mappingProcessor, subSrcClass, subDestClass, restPropertyName, destPropNames, udfFieldMap);
//            }
//            return;
//        }
//    }
//}
