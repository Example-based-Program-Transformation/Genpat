/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */

package mfix.core.parse.relation;

import mfix.common.util.Pair;
import mfix.core.parse.relation.op.AbsOperation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author: Jiajun
 * @date: 2018/12/5
 */
public class ROpt extends ObjRelation {

    /**
     * operator
     */
    private AbsOperation _operation;

    private List<RArg> _args;

    public ROpt(AbsOperation operation) {
        super(RelationKind.OPERATION);
        _operation = operation;
        _args = new LinkedList<>();
    }

    public void setOperation(AbsOperation operation) {
        _operation = operation;
    }

    public AbsOperation getOperation() {
        return _operation;
    }

    @Override
    public void addArg(RArg arg) {
        _args.add(arg);
    }

    @Override
    public String getExprString() {
        return _operation.getExprString(_args);
    }

    @Override
    protected Set<Relation> expandDownward0(Set<Relation> set) {
        set.addAll(_args);
        return set;
    }

    @Override
    public void doAbstraction(double frequency) {

    }

    @Override
    public boolean match(Relation relation, Set<Pair<Relation, Relation>> dependencies) {
        if(!super.match(relation, dependencies)) {
            return false;
        }
        ROpt opt = (ROpt) relation;
        return _operation.match(opt.getOperation());
    }

    @Override
    public String toString() {
        return getExprString();
    }
}