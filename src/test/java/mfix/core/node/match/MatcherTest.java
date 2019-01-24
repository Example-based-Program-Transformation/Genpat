/**
 * Copyright (C) SEI, PKU, PRC. - All Rights Reserved.
 * Unauthorized copying of this file via any medium is
 * strictly prohibited Proprietary and Confidential.
 * Written by Jiajun Jiang<jiajun.jiang@pku.edu.cn>.
 */

package mfix.core.node.match;

import com.sun.xml.internal.ws.policy.AssertionSet;
import mfix.common.util.Constant;
import mfix.common.util.JavaFile;
import mfix.common.util.Pair;
import mfix.core.TestCase;
import mfix.core.node.MatchInstance;
import mfix.core.node.PatternExtractor;
import mfix.core.node.ast.Node;
import mfix.core.node.modify.Modification;
import mfix.core.node.modify.Update;
import mfix.core.node.parser.NodeParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Jiajun
 * @date: 2018/9/25
 */
public class MatcherTest extends TestCase {

    @Test
    public void test_match_dont() {
        String srcFile_change_retType = testbase + Constant.SEP + "src_Intersect.java";
        String tarFile_change_retType = testbase + Constant.SEP + "tar_Intersect.java";
        CompilationUnit srcUnit = JavaFile.genASTFromFileWithType(srcFile_change_retType);
        CompilationUnit tarUnit = JavaFile.genASTFromFileWithType(tarFile_change_retType);
        List<Pair<MethodDeclaration, MethodDeclaration>> matchMap = Matcher.match(srcUnit, tarUnit);

        // The method signature cannot match
        // TODO: should permit some methods failing to match
        Assert.assertTrue(matchMap.isEmpty());
    }

    @Test
    public void test_match_do() {
        String srcFile = testbase + Constant.SEP + "src_Project.java";
        String tarFile = testbase + Constant.SEP + "tar_Project.java";

        CompilationUnit srcUnit = JavaFile.genASTFromFileWithType(srcFile);
        CompilationUnit tarUnit = JavaFile.genASTFromFileWithType(tarFile);
        List<Pair<MethodDeclaration, MethodDeclaration>> matchMap = Matcher.match(srcUnit, tarUnit);

        // all methods are well matched
        Assert.assertTrue(matchMap.size() == 108);
    }

    @Test
    public void test_modification_generation() {
        String srcFile = testbase + Constant.SEP + "src_CustomSelectionPopUp.java";
        String tarFile = testbase + Constant.SEP + "tar_CustomSelectionPopUp.java";

        Set<Node> patterns = PatternExtractor.extractPattern(srcFile, tarFile);

        // there is only one method changed
        Assert.assertTrue(patterns.size() == 1);

        Node node = patterns.iterator().next();
        // there should be only one modification, which surrounds
        // a method invocation with an if statement
        Assert.assertTrue(node.getAllModifications(new HashSet<>()).size() == 1);
        Modification modification = node.getAllModifications(new HashSet<>()).iterator().next();
        Assert.assertTrue(modification instanceof Update);
    }

    @Test
    public void test_match_demo() {
        String srcFile = testbase + Constant.SEP + "src_CustomSelectionPopUp.java";
        String tarFile = testbase + Constant.SEP + "tar_CustomSelectionPopUp.java";

        Set<Node> patterns = PatternExtractor.extractPattern(srcFile, tarFile);

        String buggy = testbase + Constant.SEP + "buggy_SimpleSecureBrowser.java";
        CompilationUnit unit = JavaFile.genASTFromFileWithType(buggy);
        final Set<MethodDeclaration> methods = new HashSet<>();
        unit.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
                methods.add(node);
                return true;
            }
        });

        NodeParser parser = NodeParser.getInstance();
        parser.setCompilationUnit(buggy, unit);
        for(MethodDeclaration m : methods) {
            Node node = parser.process(m);
            Set<Node> matched = Matcher.filter(node, patterns);
            for(Node p : matched) {
                Set<MatchInstance> set = Matcher.tryMatch(node, p);
                for (MatchInstance matchInstance : set) {
                    matchInstance.apply();
                    System.out.println("------------ Before ---------------");
                    System.out.println(node.toSrcString());
                    System.out.println("------------ After ---------------");
                    System.out.println(node.adaptModifications());
//                    System.out.println("------------ Solution ---------------");
//                    System.out.println(matchInstance.getNodeMap());
                }
            }
        }
    }

    @Test
    public void test_matcho() {
        String srcFile = testbase + Constant.SEP + "1.java";
        String tarFile = testbase + Constant.SEP + "2.java";

        Set<Node> patterns = PatternExtractor.extractPattern(srcFile, tarFile);
        for (Node node : patterns) {
            System.out.println("------------------");
            for (Modification modification : node.getAllModifications(new HashSet<>())) {
                System.out.println(modification);
            }
        }
    }


}
