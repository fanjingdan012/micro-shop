//package com.fjd.repository;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.persistence.LockModeType;
//
//import com.fjd.exception.BusinessException;
//import com.fjd.exception.ErrorCode;
//import com.fjd.exception.SystemException;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.CollectionUtils;
//
//import com.sap.sbo.bofrw.common.CurrentUserInfo;
//import com.sap.sbo.bofrw.common.bo.BORepository;
//import com.sap.sbo.bofrw.common.bo.BusinessObject;
//import com.sap.sbo.bofrw.common.bo.BusinessObjectFacade;
//import com.sap.sbo.bofrw.common.bo.BusinessObjectIdentifier;
//import com.sap.sbo.bofrw.common.bo.boql.BOQLQuery;
//import com.sap.sbo.bofrw.common.bo.query.AliasContext;
//import com.sap.sbo.bofrw.common.bo.query.Constant;
//import com.sap.sbo.bofrw.common.bo.query.Criteria;
//import com.sap.sbo.bofrw.common.bo.query.Operator.Equal;
//import com.sap.sbo.bofrw.common.bo.query.Order;
//import com.sap.sbo.bofrw.common.bo.query.Path;
//import com.sap.sbo.bofrw.common.bo.query.Selection;
//import com.sap.sbo.bofrw.common.errorcode.BOFrwNSErrorCode;
//import com.sap.sbo.bofrw.dos.DosPermission;
//import com.sap.sbo.bofrw.metadata.BusinessObjectType;
//import com.sap.sbo.bofrw.metadata.Property;
//import com.sap.sbo.bofrw.ns.JpaContext;
//import com.sap.sbo.bofrw.ns.bo.NewStackBusinessObject;
//import com.sap.sbo.bofrw.ns.bo.query.JPQLCriteriaExpressionVisitor;
//import com.sap.sbo.bofrw.ns.dos.ctrl.DosMgr;
//import com.sap.sbo.bofrw.ns.dos.ctx.DosBeanContextMgr;
//
//
///**
// * @author I076408
// *
// */
//public abstract class AbstractEntityBORepository<T extends BusinessObject> implements BORepository<T> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityBORepository.class);
//    private static final Logger PERF_LOGGER = LoggerFactory.getLogger("performance");
//    private static final String PERM_FORMAT = "{0},{1},{2},{3},{4},{5},{6}";
//
//    protected Class<T> clazz = null;
//    protected final BusinessObjectType boMeta;
//    private final BusinessObjectFacade boFacade;
//    private final JpaContext jpaContext;
//
//    public AbstractEntityBORepository(Class<T> clazz, BusinessObjectType boMeta, BusinessObjectFacade boFacade) {
//        if (clazz == null) {
//            throw new SystemException(ErrorCode.PLATFORM_BO_CLASS_NULL);
//        }
//
//        this.clazz = clazz;
//        this.boMeta = boMeta;
//        this.boFacade = boFacade;
//        this.jpaContext = boFacade.getService(JpaContext.class);
//    }
//
//    @Override
//    public T load(BusinessObjectIdentifier key) {
//
//        Object keyValue = key.getKeyValue(BusinessObjectIdentifier.NEW_STACK_BO_ID);
//        if (keyValue instanceof Integer) {
//            keyValue = ((Integer) keyValue).longValue();
//        }
//
//        return this.load(keyValue);
//    }
//
//    @Override
//    public T load(Object... keys) {
//        return load(true, keys);
//    }
//
//    @Override
//    public T load(boolean checkDataOwnership, Object... keys) {
//        long perfBegin = System.currentTimeMillis();
//
//        if (keys.length == 0) {
//            throw new BusinessException(ErrorCode.PLATFORM_KEY_ISEMPTY);
//        }
//        T bo = this.jpaContext.getEntityManager().find(clazz, keys[0]);
//
//        if (checkDataOwnership) {
//            // Check data ownership permissions
//            checkDataOwnershipPermission(bo, DosPermission.READ);
//        }
//
//        // performance log
//        if (PERF_LOGGER.isDebugEnabled()) {
//            String message = MessageFormat.format(PERM_FORMAT, "", "Repository", clazz.getSimpleName(), "Load",
//                    "NewStackBO", "", String.valueOf(System.currentTimeMillis() - perfBegin));
//            PERF_LOGGER.debug(message);
//        }
//
//        return bo;
//    }
//
//    @Override
//    public T loadForUpdate(BusinessObjectIdentifier boIdentifier) {
//        Object keyValue = boIdentifier.getKeyValue(BusinessObjectIdentifier.NEW_STACK_BO_ID);
//        if (keyValue instanceof Integer) {
//            keyValue = ((Integer) keyValue).longValue();
//        }
//
//        return this.loadForUpdate(keyValue);
//    }
//
//    @Override
//    public T loadForUpdate(Object... keys) {
//        long perfBegin = System.currentTimeMillis();
//
//        if (keys.length == 0) {
//            throw new BusinessException(BOFrwNSErrorCode.PLATFORM_KEY_ISEMPTY);
//        }
//        T bo = this.jpaContext.getEntityManager().find(clazz, keys[0], LockModeType.PESSIMISTIC_READ);
//
//        // Check data ownership permissions
//        checkDataOwnershipPermission(bo, DosPermission.READ);
//
//        // performance log
//        if (PERF_LOGGER.isDebugEnabled()) {
//            String message = MessageFormat.format(PERM_FORMAT, "", "Repository", clazz.getSimpleName(), "Load",
//                    "NewStackBO", "", String.valueOf(System.currentTimeMillis() - perfBegin));
//            PERF_LOGGER.debug(message);
//        }
//
//        return bo;
//    }
//
//    @Override
//    public T loadReference(BusinessObjectIdentifier boIdentifier) {
//        Object keyValue = boIdentifier.getKeyValue(BusinessObjectIdentifier.NEW_STACK_BO_ID);
//        if (keyValue instanceof Integer) {
//            keyValue = ((Integer) keyValue).longValue();
//        }
//
//        return this.loadReference(keyValue);
//    }
//
//    @Override
//    public T loadReference(Object... keys) {
//        if (keys.length == 0) {
//            throw new BusinessException(BOFrwNSErrorCode.PLATFORM_KEY_ISEMPTY);
//        }
//        T bo = this.jpaContext.getEntityManager().getReference(clazz, keys[0]);
//
//        // Check data ownership permissions
//        checkDataOwnershipPermission(bo, DosPermission.READ);
//
//        return bo;
//    }
//
//    @Override
//    public void save(T bo) {
//        jpaContext.getEntityManager().persist(bo);
//    }
//
//    @Override
//    public void update(T bo) {
//
//        jpaContext.getEntityManager().merge(bo);
//    }
//
//    @Override
//    public void delete(T bo) {
//
//        jpaContext.getEntityManager().remove(bo);
//    }
//
//    // currently, we only support one BK
//    // if not define bk return null
//    @Override
//    public T loadByBusinessKey(Object... keyValues) {
//        List<String> bizKey = this.boMeta.getRootNode().getBusinessKey();
//        List<String> primaryKey = this.boMeta.getRootNode().getPrimaryKey();
//        if (bizKey.get(0).equals(primaryKey.get(0))) {
//            return null;
//        }
//        Criteria cr = new Criteria();
//        cr.where(new Equal(new Path(String.class, bizKey.get(0)), new Constant(keyValues[0])));
//        List<T> boList = find(cr);
//        return boList.size() > 0 ? boList.get(0) : null;
//    }
//
//    @Override
//    public List<T> find(final Criteria cr) {
//        long perfBegin = System.currentTimeMillis();
//
//        final Map<String, Object> parameters = new HashMap<String, Object>();
//
//        final String jpql = getQueryAndParameters(cr, parameters, false, false);
//
//        List<T> boList = this.getQueryResultList(cr, parameters, jpql);
//
//        // performance log
//        if (PERF_LOGGER.isDebugEnabled()) {
//            String message = MessageFormat.format(PERM_FORMAT, "", "Repository", clazz.getSimpleName(), "find",
//                    "NewStackBO", "", String.valueOf(System.currentTimeMillis() - perfBegin));
//            PERF_LOGGER.debug(message);
//        }
//
//        return boList;
//    }
//
//    @Override
//    public List<Long> findIDs(final Criteria cr) {
//        long perfBegin = System.currentTimeMillis();
//
//        final Map<String, Object> parameters = new HashMap<String, Object>();
//
//        final String jpql = getQueryAndParameters(cr, parameters, false, true);
//
//        List<Long> ids = this.getQueryIDList(cr, parameters, jpql);
//
//        // performance log
//        if (PERF_LOGGER.isDebugEnabled()) {
//            String message = MessageFormat.format(PERM_FORMAT, "", "Repository", clazz.getSimpleName(), "findIDs",
//                    "NewStackBO", "", String.valueOf(System.currentTimeMillis() - perfBegin));
//            PERF_LOGGER.debug(message);
//        }
//        return ids;
//    }
//
//    private List<Long> getQueryIDList(final Criteria cr, final Map<String, Object> parameters, final String jpql) {
//
//        BOQLQuery<Long> tq = null;
//        if (cr != null && cr.isBypassDataOwnership()) {
//            tq = this.boFacade.createQueryWithoutAccessControl(jpql, Long.class);
//        } else {
//            tq = this.boFacade.createQuery(jpql, Long.class);
//        }
//
//        for (Entry<String, Object> entry : parameters.entrySet()) {
//            tq.setParameter(entry.getKey(), entry.getValue());
//        }
//
//        if (cr != null) {
//            if (cr.getTop() > 0) {
//                tq.setMaxResults(Long.valueOf(cr.getTop()).intValue());
//            }
//            if (cr.getSkip() > 0) {
//                tq.setFirstResult(Long.valueOf(cr.getSkip()).intValue());
//            }
//        }
//
//        return tq.getResultList();
//    }
//
//    private List<T> getQueryResultList(final Criteria cr, final Map<String, Object> parameters, final String jpql) {
//
//        BOQLQuery<T> tq = null;
//        if (cr != null && cr.isBypassDataOwnership()) {
//            tq = this.boFacade.createQueryWithoutAccessControl(jpql, clazz);
//        } else {
//            tq = this.boFacade.createQuery(jpql, clazz);
//        }
//
//        for (Entry<String, Object> entry : parameters.entrySet()) {
//            tq.setParameter(entry.getKey(), entry.getValue());
//        }
//
//        if (cr != null) {
//            if (cr.getTop() > 0) {
//                tq.setMaxResults(Long.valueOf(cr.getTop()).intValue());
//            }
//            if (cr.getSkip() > 0) {
//                tq.setFirstResult(Long.valueOf(cr.getSkip()).intValue());
//            }
//        }
//
//        return tq.getResultList();
//    }
//
//    @Override
//    public long count(Criteria cr) {
//        long perfBegin = System.currentTimeMillis();
//
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        String jpql = getQueryAndParameters(cr, parameters, true, false);
//
//        BOQLQuery<Long> tq = null;
//        if (cr != null && cr.isBypassDataOwnership()) {
//            tq = this.boFacade.createQueryWithoutAccessControl(jpql, Long.class);
//        } else {
//            tq = this.boFacade.createQuery(jpql, Long.class);
//        }
//        for (Entry<String, Object> entry : parameters.entrySet()) {
//            tq.setParameter(entry.getKey(), entry.getValue());
//        }
//
//        long retValue = tq.getSingleResult();
//
//        // performance log
//        if (PERF_LOGGER.isDebugEnabled()) {
//            String message = MessageFormat.format(PERM_FORMAT, "", "Repository", clazz.getSimpleName(), "count",
//                    "NewStackBO", "", String.valueOf(System.currentTimeMillis() - perfBegin));
//            PERF_LOGGER.debug(message);
//        }
//        return retValue;
//    }
//
//    protected String getQueryAndParameters(Criteria cr, Map<String, Object> parameters, boolean isCount,
//            boolean onlySelectId) {
//        AliasContext context = new AliasContext();
//        String aliasName = context.createAlias(boMeta.getName());
//
//        StringBuilder sb = new StringBuilder();
//        String selectionString = getSelectionString(cr, aliasName, isCount, onlySelectId);
//        String whereCondition = getWhereCondition(cr, context, isCount, parameters);
//        sb.append(selectionString).append(whereCondition);
//
//        LOGGER.debug("Generated JPQL: " + sb.toString());
//        return sb.toString();
//    }
//
//    private String getSelectionString(Criteria cr, String aliasName, boolean isCount, boolean onlySelectId) {
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT ");
//
//        if (onlySelectId) {
//            sb.append(aliasName);
//            sb.append(".");
//            sb.append(BusinessObjectIdentifier.NEW_STACK_BO_ID);
//        } else {
//            if (isCount) {
//                sb.append("COUNT(");
//                sb.append(aliasName);
//                sb.append(")");
//            } else if (cr == null || CollectionUtils.isEmpty(cr.getSelectionList())) {
//                sb.append(aliasName);
//            } else {
//                int count = cr.getSelectionList().size();
//                for (Selection s : cr.getSelectionList()) {
//                    sb.append(aliasName);
//                    sb.append(".");
//                    sb.append(s.getAlias());
//                    if (count > 1)
//                        sb.append(",");
//                    count--;
//                }
//            }
//        }
//
//        sb.append(" ");
//
//        sb.append("FROM ");
//        sb.append(boMeta.getName());
//        sb.append(" ");
//        sb.append(aliasName);
//        sb.append(" ");
//
//        return sb.toString();
//    }
//
//    protected StringBuilder addExtraCondition(StringBuilder sb, String tableAials) {
//        return sb;
//    }
//
//    public String getWhereCondition(Criteria cr, AliasContext aliasContext, boolean isCount,
//            Map<String, Object> parameters) {
//        StringBuilder sb = new StringBuilder();
//        String aliasName = aliasContext.getAlias(boMeta.getName());
//        if (cr == null) {
//            sb = addExtraCondition(sb, aliasName);
//            if (sb.length() == 0) {
//                return sb.toString();
//            } else {
//                StringBuilder sbNew = new StringBuilder();
//                sbNew.append("WHERE ").append(sb);
//                return sbNew.toString();
//            }
//        }
//
//        boolean extraWhereCondition = false;
//        if (cr.getRestriction() != null) {
//            JPQLCriteriaExpressionVisitor visitor = new JPQLCriteriaExpressionVisitor(boMeta, parameters, aliasContext);
//            cr.getRestriction().accept(visitor);
//
//            if (visitor.getWhere().length() > 0) {
//                sb.append("WHERE ");
//            }
//            sb.append(addExtraCondition(visitor.getWhere(), aliasName));
//        } else {
//            StringBuilder sbNew = new StringBuilder();
//            sbNew = addExtraCondition(sbNew, aliasName);
//            if (sbNew.length() > 0) {
//                sb.append("WHERE ").append(sbNew);
//                extraWhereCondition = true;
//            }
//        }
//
//        if (StringUtils.isNotEmpty(cr.getKeyword())) {
//            sb.append(getSearchString(aliasName, cr.getKeyword(), parameters,
//                    (cr.getRestriction() != null) || extraWhereCondition));
//        }
//
//        if (!isCount && !CollectionUtils.isEmpty(cr.getOrderList())) {
//            sb.append("ORDER BY ");
//            int count = cr.getOrderList().size();
//            for (Order o : cr.getOrderList()) {
//                sb.append(aliasName);
//                sb.append(".");
//                sb.append(o.getExpression().getAlias());
//                if (o.isAscending())
//                    sb.append(" asc");
//                else
//                    sb.append(" desc");
//                if (count > 1)
//                    sb.append(",");
//                count--;
//            }
//        }
//
//        return sb.toString();
//    }
//
//    public String getSearchString(String tableAlias, String keyword, Map<String, Object> parameters,
//            boolean hasCondition) {
//        StringBuilder sb = new StringBuilder();
//        List<String> searchables = new ArrayList<>();
//        List<Property> properties = boMeta.getRootNode().getProperties();
//        for (Property property : properties) {
//            if (property.isSearchable()) {
//                if (!boMeta.getRootNode().isUserField(property.getFullName())) {
//                    String searchPath = property.getSearchPath();
//                    if (StringUtils.isBlank(searchPath)) {
//                        searchables.add(tableAlias + "." + property.getFullName());
//                    } else {
//                        searchables.add(tableAlias + "." + searchPath);
//                    }
//                }
//            }
//        }
//
//        if (!searchables.isEmpty()) {
//            if (!hasCondition) {
//                sb.append("WHERE ");
//            } else {
//                sb.append(" AND ");
//            }
//            sb.append("(");
//            Boolean escape = keyword.contains("%") || keyword.contains("_") || keyword.contains("\\");
//            String loKeyword = keyword.toLowerCase().replaceAll("\\\\", "\\\\\\\\").replaceAll("%", "\\\\%")
//                    .replaceAll("_", "\\\\_");
//            int paramPos = parameters.size();
//            for (int ii = 0; ii < searchables.size(); ii++) {
//                paramPos++;
//                String condition = String.format("LOWER(%s) LIKE ?%s", searchables.get(ii), paramPos);
//                parameters.put(String.valueOf(paramPos), "%" + loKeyword + "%");
//                sb.append(condition);
//                String likeEscape = " ESCAPE '\\' ";
//                if (escape) {
//                    sb.append(likeEscape);
//                }
//                if (ii != searchables.size() - 1) {
//                    sb.append(" OR ");
//                }
//            }
//            sb.append(")");
//        }
//
//        return sb.toString();
//    }
//
//    @Override
//    public BusinessObjectType getBusinessObjectType() {
//        return this.boMeta;
//    }
//
//    public BusinessObjectFacade getBoFacade() {
//        return boFacade;
//    }
//
//    @Override
//    public void flush() {
//        this.jpaContext.getEntityManager().flush();
//    }
//
//    /**
//     * check whether the current user has requested data ownership permission
//     *
//     * @param requestedPerm
//     * @param bo
//     */
//    private void checkDataOwnershipPermission(T bo, DosPermission requestedPerm) {
//
//        if (bo == null || !(bo instanceof NewStackBusinessObject)) {
//            return;
//        }
//        DosMgr dosMgr = DosBeanContextMgr.getDosMgr(boFacade);
//        String boName = this.getBusinessObjectType().getName();
//        String boNamespace = this.getBusinessObjectType().getNamespace();
//        CurrentUserInfo userInfo = boFacade.getCurrentUser();
//
//        if (userInfo != null) {
//            Long targetEmployeeId = userInfo.getEmployeeId();
//            if (targetEmployeeId == null) {
//                return;
//            }
//        }
//        if (!dosMgr.isBoAccessible(boNamespace, boName, (NewStackBusinessObject) bo, boFacade, requestedPerm)) {
//            // normal business behavior, info log level
//            LOGGER.info(
//                    "DOS: access to BO instance is rejected! Namespace-{}, BO-{}, BOID-{}, Requested permission-{}",
//                    boNamespace, boName, ((NewStackBusinessObject) bo).getId(), requestedPerm.toString());
//            String boRawLabel = boFacade.getMetadataRepository().getNodeType(boNamespace, boName).getRawLabel();
//            String boLocalizedName = boFacade.getMessageBundle().getString(boRawLabel);
//            String permLocalized = boFacade.getMessageBundle().getString("dosrps." + requestedPerm.toString());
//            throw new BusinessException(BOFrwNSErrorCode.NO_DATAOWNERSHIP_PERMISSION, permLocalized, boLocalizedName,
//                    ((NewStackBusinessObject) bo).getBusinessCodeForMsgReport());
//        }
//    }
//}
