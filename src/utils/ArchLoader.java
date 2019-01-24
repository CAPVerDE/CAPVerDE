package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import architecture.Action;
import architecture.Component;
import architecture.Composition;
import architecture.DataType;
import architecture.Deduction;
import architecture.DeductionCapability;
import architecture.Dep;
import architecture.DependenceRelation;
import architecture.Equation;
import architecture.Term;
import architecture.Variable;
import properties.Property;
import properties.Property.PropertyType;
import architecture.Action.ActionType;
import architecture.Equation.Relation;
import architecture.Equation.Type;
import architecture.Purpose;
import architecture.PurposeHierarchy;
import architecture.Term.Operator;
import architecture.Term.OperatorType;
import architecture.Term.TermType;
import architecture.Trust;
import gui.ArchitectureFunctions;
import gui.ArchitectureFunctions.CaseStudy;

/**
 * Class that helps loading the pre-existing case studies.
 */
public class ArchLoader {

	// class fields
	//########## Smart Home #################
	// Components
	private static Component SM = new Component("SM");
	private static Component MI = new Component("MI");
	private static Component Re = new Component("Re");
	private static Component HN = new Component("HN");
	private static Set<Component> cSet1 = Stream.of(SM, MI, Re, HN).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable readings = new Variable("readings");
	private static Variable k = new Variable("k");
	private static Variable bill = new Variable("bill");
	private static Variable pw = new Variable("pw");
	private static Variable secret = new Variable("secret");
	private static Variable encBill = new Variable("encBill");
	private static Variable encR = new Variable("encR");
	private static Variable ppd = new Variable("ppd");
	private static Set<Variable> vSet1 = Stream.of(readings, k, bill, pw, secret, encBill, encR, ppd).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termReadings = new Term(TermType.ATOM, readings, false);
	private static Term termK = new Term(TermType.ATOM, k, false);
	private static Term termBill = new Term(TermType.ATOM, bill, false);
	private static Term termPw = new Term(TermType.ATOM, pw, false);
	private static Term termSecret = new Term(TermType.ATOM, secret, false);
	private static Term termPpd = new Term(TermType.ATOM, ppd, false);
	private static Term termEncB = new Term(TermType.ATOM, encBill, false);
	private static Term termEncR = new Term(TermType.ATOM, encR, false);
	private static Term termBetaReadings = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "beta", termReadings, false);
	private static Term termEncReadings = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Enc", termReadings, termK, false);
	private static Term termDecReadings = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Dec", termEncR, termK, false);
	private static Term termEncBill = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Enc", termBill, termK, false);
	private static Term termDecBill = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "Dec", termEncB, termK, false);
	private static Term termPhiReadings = new Term(
			TermType.COMPOSITION, OperatorType.TERTIARY, Operator.FUNC, "phi", termReadings, termBill, termPw, false);
	private static Term termPhiInvReadings = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "phi^-1", termPpd, termPw, false);
	private static Term termPhiInvBill = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "phi^-1", termPpd, termPw, false);
	private static Set<Term> tSet1 = Stream.of(
			termReadings, termK, termBill, termPw, termSecret, termPpd, termEncB, termEncR,
			termBetaReadings, termEncReadings, termDecReadings, termEncBill, termDecBill,
			termPhiReadings, termPhiInvReadings, termPhiInvBill).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation encR_enc = new Equation(
			"encR_enc", Type.RELATION, Relation.EQUALITY, termEncR, termEncReadings);
	private static Equation bill_dec = new Equation(
			"bill_dec", Type.RELATION, Relation.EQUALITY, termBill, termDecBill);
	private static Equation ppd_phi = new Equation(
			"ppd_phi", Type.RELATION, Relation.EQUALITY, termPpd, termPhiReadings);
	private static Equation readings_dec = new Equation(
			"readings_dec", Type.RELATION, Relation.EQUALITY, termReadings, termDecReadings);
	private static Equation bill_beta = new Equation(
			"bill_beta", Type.RELATION, Relation.EQUALITY, termBill, termBetaReadings);
	private static Equation encBill_enc = new Equation(
			"encBill_enc", Type.RELATION, Relation.EQUALITY, termEncB, termEncBill);
	private static Equation readings_phiInv = new Equation(
			"readings_phiInv", Type.RELATION, Relation.EQUALITY, termReadings, termPhiInvReadings);
	private static Equation bill_phiInv = new Equation(
			"bill_phiInv", Type.RELATION, Relation.EQUALITY, termBill, termPhiInvBill);
	private static Set<Equation> eSet1 = Stream.of(
			encR_enc, bill_dec, ppd_phi, readings_dec, bill_beta, encBill_enc, readings_phiInv, bill_phiInv).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet1 = new LinkedHashSet<Trust>();
	// Compositions
	private static Set<Composition> composSet1 = new LinkedHashSet<Composition>();
	// Statements
	private static Set<architecture.Statement> stSet1 = new LinkedHashSet<architecture.Statement>();
	// Actions
	private static Action hasSM_readings = new Action(ActionType.HAS, SM, readings);
	private static Action hasSM_pw = new Action(ActionType.HAS, SM, pw);
	private static Action hasSM_k = new Action(ActionType.HAS, SM, k);
	private static Action hasMI_k = new Action(ActionType.HAS, MI, k);
	private static Action hasRe_pw = new Action(ActionType.HAS, Re, pw);
	private static Action hasRe_secret = new Action(ActionType.HAS, Re, secret);
	private static Action computeSM_encR = new Action(
			ActionType.COMPUTE, SM, encR_enc);
	private static Action computeSM_bill = new Action(
			ActionType.COMPUTE, SM, bill_dec);
	private static Action computeSM_ppd = new Action(
			ActionType.COMPUTE, SM, ppd_phi);
	private static Action computeMI_readings = new Action(
			ActionType.COMPUTE, MI, readings_dec);
	private static Action computeMI_bill = new Action(
			ActionType.COMPUTE, MI, bill_beta);
	private static Action computeMI_encBill = new Action(
			ActionType.COMPUTE, MI, encBill_enc);
	private static Action computeRe_readings = new Action(
			ActionType.COMPUTE, Re, readings_phiInv);
	private static Action computeRe_bill = new Action(
			ActionType.COMPUTE, Re, bill_phiInv);
	private static Action receiveHNSM1 = new Action(
			ActionType.RECEIVE, HN, SM, Collections.emptySet(), Set.of(encR));
	private static Action receiveSMHN = new Action(
			ActionType.RECEIVE, SM, HN, Collections.emptySet(), Set.of(encBill));
	private static Action receiveHNSM2 = new Action(
			ActionType.RECEIVE, HN, SM, Collections.emptySet(), Set.of(ppd));
	private static Action receiveMIHN = new Action(
			ActionType.RECEIVE, MI, HN, Collections.emptySet(), Set.of(encR));
	private static Action receiveHNMI = new Action(
			ActionType.RECEIVE, HN, MI, Collections.emptySet(), Set.of(encBill));
	private static Action receiveReHN = new Action(
			ActionType.RECEIVE, Re, HN, Collections.emptySet(), Set.of(ppd));
	private static Action checkRe = new Action(
			ActionType.CHECK, Re, Set.of(bill_beta));
	private static Set<Action> aSet1 = Stream.of(
			hasSM_readings, hasSM_pw, hasSM_k, hasMI_k, hasRe_pw, hasRe_secret, computeSM_encR, computeSM_bill,
			computeSM_ppd, computeMI_readings, computeMI_bill, computeMI_encBill, computeRe_readings,
			computeRe_bill, receiveHNSM1, receiveSMHN, receiveHNSM2, receiveMIHN, receiveHNMI, receiveReHN, checkRe).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	private static Dep dep1 = new Dep(pw, Collections.emptySet(), 0.001);
	private static Dep dep2 = new Dep(readings, Set.of(encR), 0.00001);
	private static Dep dep3 = new Dep(readings, Set.of(ppd, pw), 1);
	private static Dep dep4 = new Dep(secret, Collections.emptySet(), 0.01);
	private static Dep dep5 = new Dep(pw, Set.of(secret), 1);
	private static Set<DependenceRelation> dSet1 = Stream.of(
			new DependenceRelation(HN, dep1), new DependenceRelation(HN, dep2),
			new DependenceRelation(HN, dep3), new DependenceRelation(HN, dep4),
			new DependenceRelation(HN, dep5)).collect(Collectors.toCollection(LinkedHashSet::new));
	// Deductions
	private static Variable varT = new Variable("t");
	private static Term termT = new Term(TermType.ATOM, varT, true);
	private static Variable varU = new Variable("u");
	private static Term termU = new Term(TermType.ATOM, varU, true);
	private static Variable varX = new Variable("x");
	private static Variable varY = new Variable("y");
	private static Term termX = new Term(TermType.ATOM, varX, true);
	private static Term termY = new Term(TermType.ATOM, varY, true);
	private static Equation dedEq2 = new Equation(
			"subst", Type.RELATION, Relation.EQUALITY, termT, termU);
	private static Equation dedEq3 = new Equation(
			"dedEq3", Type.RELATION, Relation.EQUALITY, termX, termY);
	private static Set<Equation> dedEqSet2 = Set.of(dedEq2, dedEq3);
	private static Deduction deduc4 = new Deduction(
			Deduction.Type.SUBST, dedEqSet2, dedEq2, "Substitution", 1);
	private static DeductionCapability dc_SM = new DeductionCapability(SM, Set.of(deduc4));
	private static DeductionCapability dc_MI = new DeductionCapability(MI, Set.of(deduc4));
	private static DeductionCapability dc_Re = new DeductionCapability(Re, Set.of(deduc4));
	private static DeductionCapability dc_HN = new DeductionCapability(HN, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet1 = Stream.of(dc_SM, dc_MI, dc_Re, dc_HN).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property statement1 = new Property(PropertyType.KNOWS, Re, (double)1, bill_beta);
	private static Property statement2_tmp = new Property(PropertyType.HAS, HN, 0.001, readings);
	private static Property statement2 = new Property(PropertyType.NEGATION, statement2_tmp);
	//TODO 1 more?
	private static Set<Property> pSet1 = Stream.of(statement1, statement2).collect(Collectors.toCollection(LinkedHashSet::new));

	//############## AccuWeather ###############
	// Components
	private static Component U = new Component("U");
	private static Component AW = new Component("AW");
	private static Component RM = new Component("RM");
	private static Set<Component> cSet2 = Stream.of(U, AW, RM).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable location = new Variable("location");
	private static Variable wifi_info = new Variable("wifi_info");
	private static Variable weather = new Variable("weather");
	private static Set<Variable> vSet2 = Stream.of(location, wifi_info, weather).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termLocation = new Term(TermType.ATOM, location, false);
	private static Term termWifi_info = new Term(TermType.ATOM, wifi_info, false);
	private static Term termWeather = new Term(TermType.ATOM, weather, false);
	private static Term termPhiWifi = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "phi", termWifi_info, false);
	private static Set<Term> tSet2 = Stream.of(
			termLocation, termWifi_info, termWeather, termPhiWifi).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation location_phi = new Equation(
			"location_phi", Type.RELATION, Relation.EQUALITY, termLocation, termPhiWifi);
	private static Set<Equation> eSet2 = Stream.of(location_phi).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet2 = new LinkedHashSet<Trust>();
	// Compositions
	private static Set<Composition> composSet2 = new LinkedHashSet<Composition>();
	// Statements
	private static Set<architecture.Statement> stSet2 = new LinkedHashSet<architecture.Statement>();
	// Actions
	private static Action hasU_location = new Action(ActionType.HAS, U, location);
	private static Action hasU_wifi_info = new Action(ActionType.HAS, U, wifi_info);
	private static Action hasAW_weather = new Action(ActionType.HAS, AW, weather);
	private static Action computeRM_location = new Action(
			ActionType.COMPUTE, RM, location_phi);
	private static Action receiveAWU = new Action(
			ActionType.RECEIVE, AW, U, Collections.emptySet(), Set.of(wifi_info));
	private static Action receiveRMAW = new Action(
			ActionType.RECEIVE, RM, AW, Collections.emptySet(), Set.of(wifi_info));
	private static Action receiveUAW = new Action(
			ActionType.RECEIVE, U, AW, Collections.emptySet(), Set.of(weather));
	private static Set<Action> aSet2 = Stream.of(
			hasU_location, hasU_wifi_info, hasAW_weather, computeRM_location, receiveAWU, receiveRMAW, receiveUAW).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	private static Dep dep = new Dep(location, Set.of(wifi_info), 0.5);
	private static Set<DependenceRelation> dSet2 = Stream.of(
			new DependenceRelation(AW, dep), new DependenceRelation(RM, dep)).collect(Collectors.toCollection(LinkedHashSet::new));
	// Deductions
	private static DeductionCapability dc_U = new DeductionCapability(U, Set.of(deduc4));
	private static DeductionCapability dc_AW = new DeductionCapability(AW, Set.of(deduc4));
	private static DeductionCapability dc_RM = new DeductionCapability(RM, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet2 = Stream.of(dc_U, dc_AW, dc_RM).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property property_accuracy = new Property(PropertyType.HAS, U, (double)1, weather);
	private static Property property_tmp = new Property(PropertyType.HAS, AW, 0.001, location);
	private static Property property_dataMinimisation = new Property(PropertyType.NEGATION, property_tmp);
	private static Property property_dataMinimisation2 = new Property(PropertyType.NOTSHARED, AW, wifi_info);
	//TODO 1 more?
	private static Set<Property> pSet2 = Stream.of(property_accuracy, property_dataMinimisation, property_dataMinimisation2).collect(Collectors.toCollection(LinkedHashSet::new));

	//########## Patient Data Register #################
	// Components
	private static Component M = new Component("M");
	private static Component CR = new Component("CR");
	private static Component MAi = new Component("MA", "i");
	private static Component SA = new Component("SA");
	private static Component MCi = new Component("MC", "i");
	private static Component Pj = new Component("P", "j");
	private static Component Rl = new Component("R", "l");
	private static Component MDik = new Component("MD", "ik");
	private static Component IDBi = new Component("IDB", "i");
	private static Component RDBi = new Component("RDB", "i");
	private static Set<Component> cSet3 = Stream.of(M, CR, MAi, SA, MCi, Pj, Rl, MDik, IDBi, RDBi).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable cki = new Variable("ck", "i");
	private static Variable iki = new Variable("ik", "i");
	private static Variable pukik = new Variable("puk", "ik");
	private static Variable eckik = new Variable("eck", "ik");
	private static Variable eikik = new Variable("eik", "ik");
	private static Variable pDj = new Variable("pD", "j");
	private static Variable mDj = new Variable("mD", "j");
	private static Variable eukik = new Variable("euk", "ik");
	private static Variable pubMk = new Variable("pubMk");
	private static Variable privMk = new Variable("privMk");
	private static Variable pwkik = new Variable("pwk", "ik");
	private static Variable rkj = new Variable("rk", "j");
	private static Variable ukik = new Variable("uk", "ik");
	private static Variable epDj = new Variable("epD", "j");
	private static Variable emDj = new Variable("emD", "j");
	private static Variable cerkj = new Variable("cerk", "j");
	private static Variable merkj = new Variable("merk", "j");
	private static Variable amDj = new Variable("amD", "j");
	private static Variable stats = new Variable("stats");
	private static Set<Variable> vSet3 = Stream.of(cki, iki, pukik, eckik, eikik, pDj, mDj, eukik, pubMk, privMk, pwkik,
			rkj, ukik, epDj, emDj, cerkj, merkj, amDj, stats).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termCki = new Term(TermType.ATOM, cki, false);
	private static Term termIki = new Term(TermType.ATOM, iki, false);
	private static Term termPukik = new Term(TermType.ATOM, pukik, false);
	private static Term termEckik = new Term(TermType.ATOM, eckik, false);
	private static Term termEikik = new Term(TermType.ATOM, eikik, false);
	private static Term termPDj = new Term(TermType.ATOM, pDj, false);
	private static Term termMDj = new Term(TermType.ATOM, mDj, false);
	private static Term termEukik = new Term(TermType.ATOM, eukik, false);
	private static Term termPubMk = new Term(TermType.ATOM, pubMk, false);
	private static Term termPrivMk = new Term(TermType.ATOM, privMk, false);
	private static Term termPwkik = new Term(TermType.ATOM, pwkik, false);
	private static Term termRkj = new Term(TermType.ATOM, rkj, false);
	private static Term termUkik = new Term(TermType.ATOM, ukik, false);
	private static Term termEpDj = new Term(TermType.ATOM, epDj, false);
	private static Term termEmDj = new Term(TermType.ATOM, emDj, false);
	private static Term termCerkj = new Term(TermType.ATOM, cerkj, false);
	private static Term termMerkj = new Term(TermType.ATOM, merkj, false);
	private static Term termAmDj = new Term(TermType.ATOM, amDj, false);
	private static Term termStats = new Term(TermType.ATOM, stats, false);
	private static Term termEval = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "eval", termAmDj, false);
	private static Term termDecMerkj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termMerkj, termPrivMk, false);
	private static Term termDecEmDj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEmDj, termDecMerkj, false);
	private static Term termAnon = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "anon", termDecEmDj, false);
	private static Term termEncIki = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termIki, termPukik, false);
	private static Term termEncCki = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termCki, termPukik, false);
	private static Term termDecEukik = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEukik, termPwkik, false);
	private static Term termDecEckik = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEckik, termUkik, false);
	private static Term termDecEikik = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "dec", termEikik, termUkik, false);
	private static Term termEncPDj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termPDj, termIki, false);
	private static Term termEncMDj = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termMDj, termRkj, false);
	private static Term termEncRkj1 = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termRkj, termCki, false);
	private static Term termEncRkj2 = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "enc", termRkj, termPubMk, false);
	private static Set<Term> tSet3 = Stream.of(
			termCki, termIki, termPukik, termEckik, termEikik, termPDj, termMDj, termEukik, termPubMk, termPrivMk,
			termPwkik, termRkj, termUkik, termEpDj, termEmDj, termCerkj, termMerkj, termAmDj, termStats, termEval,
			termDecEmDj, termAnon, termEncIki, termEncCki, termDecEukik, termDecEckik, termDecEikik, termEncPDj,
			termEncMDj, termEncRkj1, termEncRkj2, termDecMerkj).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation eck_enc = new Equation(
			"eck_enc", Type.RELATION, Relation.EQUALITY, termEckik, termEncCki);
	private static Equation eik_enc = new Equation(
			"eik_enc", Type.RELATION, Relation.EQUALITY, termEikik, termEncIki);
	private static Equation uk_dec = new Equation(
			"uk_dec", Type.RELATION, Relation.EQUALITY, termUkik, termDecEukik);
	private static Equation ck_dec = new Equation(
			"ck_dec", Type.RELATION, Relation.EQUALITY, termCki, termDecEckik);
	private static Equation ik_dec = new Equation(
			"ik_dec", Type.RELATION, Relation.EQUALITY, termIki, termDecEikik);
	private static Equation emD_enc = new Equation(
			"emD_enc", Type.RELATION, Relation.EQUALITY, termEmDj, termEncMDj);
	private static Equation epD_enc = new Equation(
			"epD_enc", Type.RELATION, Relation.EQUALITY, termEpDj, termEncPDj);
	private static Equation cerk_enc = new Equation(
			"cerk_enc", Type.RELATION, Relation.EQUALITY, termCerkj, termEncRkj1);
	private static Equation merk_enc = new Equation(
			"merk_enc", Type.RELATION, Relation.EQUALITY, termMerkj, termEncRkj2);
	private static Equation amD_anon = new Equation(
			"amD_anon", Type.RELATION, Relation.EQUALITY, termAmDj, termAnon);
	private static Equation stats_eval = new Equation(
			"stats_eval", Type.RELATION, Relation.EQUALITY, termStats, termEval);
	private static Set<Equation> eSet3 = Stream.of(
			eck_enc, eik_enc, uk_dec, ck_dec, ik_dec, emD_enc, epD_enc, cerk_enc, merk_enc, amD_anon, stats_eval).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet3 = new LinkedHashSet<Trust>();
	// Compositions and Associations
	private static Composition MCiMAi = new Composition(MCi, MAi);
	private static Composition MCiMDik = new Composition(MCi, MDik);
	private static Composition CRSA = new Composition(CR, SA);
	private static Composition IDBiCR = new Composition(IDBi, CR); //TODO right now db-assoc = compos from DB to component?
	private static Composition RDBiCR = new Composition(RDBi, CR);
	private static Set<Composition> composSet3 = Stream.of(MCiMAi, MCiMDik, CRSA, IDBiCR, RDBiCR).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Set<architecture.Statement> stSet3 = new LinkedHashSet<architecture.Statement>();
	// Actions
	private static Action hasMA_ck = new Action(ActionType.HAS, MAi, cki);
	private static Action hasMA_ik = new Action(ActionType.HAS, MAi, iki);
	private static Action hasMA_puk = new Action(ActionType.HAS, MAi, pukik);
	private static Action hasP_pD = new Action(ActionType.HAS, Pj, pDj);
	private static Action hasMC_mD = new Action(ActionType.HAS, MCi, mDj);
	private static Action hasMC_euk = new Action(ActionType.HAS, MCi, eukik);
	private static Action hasMC_pubMk = new Action(ActionType.HAS, MCi, pubMk);
	private static Action hasMD_pwk = new Action(ActionType.HAS, MDik, pwkik);
	private static Action hasMD_rk = new Action(ActionType.HAS, MDik, rkj);
	private static Action hasCR_privMk = new Action(ActionType.HAS, CR, privMk);
	private static Action computeMA_eck = new Action(ActionType.COMPUTE, MAi, eck_enc);
	private static Action computeMA_eik = new Action(ActionType.COMPUTE, MAi, eik_enc);
	private static Action computeMD_uk = new Action(ActionType.COMPUTE, MDik, uk_dec);
	private static Action computeMD_ck = new Action(ActionType.COMPUTE, MDik, ck_dec);
	private static Action computeMD_ik = new Action(ActionType.COMPUTE, MDik, ik_dec);
	private static Action computeMD_epD = new Action(ActionType.COMPUTE, MDik, epD_enc);
	private static Action computeMD_emD = new Action(ActionType.COMPUTE, MDik, emD_enc);
	private static Action computeMD_cerk = new Action(ActionType.COMPUTE, MDik, cerk_enc);
	private static Action computeMD_merk = new Action(ActionType.COMPUTE, MDik, merk_enc);
	private static Action computeCR_amD = new Action(ActionType.COMPUTE, CR, amD_anon);
	private static Action computeR_stats = new Action(ActionType.COMPUTE, Rl, stats_eval);
	private static Action receiveMDMA = new Action(ActionType.RECEIVE, MDik, MAi, Collections.emptySet(), Set.of(eckik, eikik));
	private static Action receiveMCP = new Action(ActionType.RECEIVE, MCi, Pj, Collections.emptySet(), Set.of(pDj));
	private static Action receiveCRMD = new Action(ActionType.RECEIVE, CR, MDik, Collections.emptySet(), Set.of(emDj, merkj, cerkj, epDj));
	private static Action receiveRCR = new Action(ActionType.RECEIVE, Rl, CR, Collections.emptySet(), Set.of(amDj));
	private static Action receiveIDBCR = new Action(ActionType.RECEIVE, IDBi, CR, Collections.emptySet(), Set.of(epDj));
	private static Action receiveRDBCR = new Action(ActionType.RECEIVE, RDBi, CR, Collections.emptySet(), Set.of(cerkj, merkj, emDj));
	private static Action spotcheckMTC = new Action(ActionType.SPOTCHECK, M, MCi, Set.of(pDj, mDj));
	private static Action spotcheckMCR = new Action(ActionType.SPOTCHECK, M, CR, Set.of(epDj, emDj));
	private static Set<Action> aSet3 = Stream.of(
			hasMA_ck, hasMA_ik, hasMA_puk, hasP_pD, hasMC_mD, hasMC_euk, hasMC_pubMk, hasMD_pwk, hasMD_rk,
			hasCR_privMk, computeMA_eck, computeMA_eik, computeMD_uk, computeMD_ck, computeMD_ik, computeMD_epD, computeMD_emD, computeMD_cerk,
			computeMD_merk, computeCR_amD, computeR_stats, receiveMDMA, receiveMCP, receiveCRMD,
			receiveRCR, receiveIDBCR, receiveRDBCR, spotcheckMTC, spotcheckMCR).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	//TODO more and more practical values?
	private static Dep patient_dep1 = new Dep(cki, Set.of(eckik, ukik), 1);
	private static Dep patient_dep2 = new Dep(iki, Set.of(eikik, ukik), 1);
	private static Dep patient_dep3 = new Dep(ukik, Set.of(eukik, pwkik), 1);
	private static Dep patient_dep4 = new Dep(rkj, Set.of(merkj, privMk), 1);
	private static Dep patient_dep5 = new Dep(rkj, Set.of(cerkj, cki), 1);
	private static Dep patient_dep6 = new Dep(pukik, Collections.emptySet(), 1);
	private static Dep patient_dep7 = new Dep(pubMk, Collections.emptySet(), 1);
	private static Dep patient_dep8 = new Dep(privMk, Collections.emptySet(), 0.0001);
	private static Dep patient_dep9 = new Dep(cki, Collections.emptySet(), 0.0001);
	private static Dep patient_dep10 = new Dep(iki, Collections.emptySet(), 0.0001);
	private static Dep patient_dep11 = new Dep(pwkik, Collections.emptySet(), 0.0001);
	private static Dep patient_dep12 = new Dep(rkj, Collections.emptySet(), 0.0001);
	private static Dep patient_dep13 = new Dep(pDj, Set.of(epDj, iki), 1);
	private static Dep patient_dep14 = new Dep(mDj, Set.of(emDj, rkj), 1);
	private static Set<DependenceRelation> dSet3 = Stream.of(
			new DependenceRelation(MAi, patient_dep1), new DependenceRelation(MAi, patient_dep2),
			new DependenceRelation(MAi, patient_dep3), new DependenceRelation(MAi, patient_dep4),
			new DependenceRelation(MAi, patient_dep5), new DependenceRelation(MAi, patient_dep6),
			new DependenceRelation(MAi, patient_dep7), new DependenceRelation(MAi, patient_dep8),
			new DependenceRelation(MAi, patient_dep9), new DependenceRelation(MAi, patient_dep10),
			new DependenceRelation(MAi, patient_dep11), new DependenceRelation(MAi, patient_dep12),
			new DependenceRelation(MAi, patient_dep13), new DependenceRelation(MAi, patient_dep14),
			new DependenceRelation(MDik, patient_dep1), new DependenceRelation(MDik, patient_dep2),
			new DependenceRelation(MDik, patient_dep3), new DependenceRelation(MDik, patient_dep4),
			new DependenceRelation(MDik, patient_dep5), new DependenceRelation(MDik, patient_dep6),
			new DependenceRelation(MDik, patient_dep7), new DependenceRelation(MDik, patient_dep8),
			new DependenceRelation(MDik, patient_dep9), new DependenceRelation(MDik, patient_dep10),
			new DependenceRelation(MDik, patient_dep11), new DependenceRelation(MDik, patient_dep12),
			new DependenceRelation(MDik, patient_dep13), new DependenceRelation(MDik, patient_dep14),
			new DependenceRelation(MCi, patient_dep1), new DependenceRelation(MCi, patient_dep2),
			new DependenceRelation(MCi, patient_dep3), new DependenceRelation(MCi, patient_dep4),
			new DependenceRelation(MCi, patient_dep5), new DependenceRelation(MCi, patient_dep6),
			new DependenceRelation(MCi, patient_dep7), new DependenceRelation(MCi, patient_dep8),
			new DependenceRelation(MCi, patient_dep9), new DependenceRelation(MCi, patient_dep10),
			new DependenceRelation(MCi, patient_dep11), new DependenceRelation(MCi, patient_dep12),
			new DependenceRelation(MCi, patient_dep13), new DependenceRelation(MCi, patient_dep14),
			new DependenceRelation(SA, patient_dep1), new DependenceRelation(SA, patient_dep2),
			new DependenceRelation(SA, patient_dep3), new DependenceRelation(SA, patient_dep4),
			new DependenceRelation(SA, patient_dep5), new DependenceRelation(SA, patient_dep6),
			new DependenceRelation(SA, patient_dep7), new DependenceRelation(SA, patient_dep8),
			new DependenceRelation(SA, patient_dep9), new DependenceRelation(SA, patient_dep10),
			new DependenceRelation(SA, patient_dep11), new DependenceRelation(SA, patient_dep12),
			new DependenceRelation(SA, patient_dep13), new DependenceRelation(SA, patient_dep14),
			new DependenceRelation(CR, patient_dep1), new DependenceRelation(CR, patient_dep2),
			new DependenceRelation(CR, patient_dep3), new DependenceRelation(CR, patient_dep4),
			new DependenceRelation(CR, patient_dep5), new DependenceRelation(CR, patient_dep6),
			new DependenceRelation(CR, patient_dep7), new DependenceRelation(CR, patient_dep8),
			new DependenceRelation(CR, patient_dep9), new DependenceRelation(CR, patient_dep10),
			new DependenceRelation(CR, patient_dep11), new DependenceRelation(CR, patient_dep12),
			new DependenceRelation(CR, patient_dep13), new DependenceRelation(CR, patient_dep14),
			new DependenceRelation(Rl, patient_dep1), new DependenceRelation(Rl, patient_dep2),
			new DependenceRelation(Rl, patient_dep3), new DependenceRelation(Rl, patient_dep4),
			new DependenceRelation(Rl, patient_dep5), new DependenceRelation(Rl, patient_dep6),
			new DependenceRelation(Rl, patient_dep7), new DependenceRelation(Rl, patient_dep8),
			new DependenceRelation(Rl, patient_dep9), new DependenceRelation(Rl, patient_dep10),
			new DependenceRelation(Rl, patient_dep11), new DependenceRelation(Rl, patient_dep12),
			new DependenceRelation(Rl, patient_dep13), new DependenceRelation(Rl, patient_dep14)).collect(Collectors.toCollection(LinkedHashSet::new));
	// Deductions
	private static DeductionCapability dc_MAi = new DeductionCapability(MAi, Set.of(deduc4));
	private static DeductionCapability dc_MCi = new DeductionCapability(MCi, Set.of(deduc4));
	private static DeductionCapability dc_MDik = new DeductionCapability(MDik, Set.of(deduc4));
	private static DeductionCapability dc_SA = new DeductionCapability(SA, Set.of(deduc4));
	private static DeductionCapability dc_CR = new DeductionCapability(CR, Set.of(deduc4));
	private static DeductionCapability dc_Rl = new DeductionCapability(Rl, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet3 = Stream.of(dc_MAi, dc_MCi, dc_MDik, dc_SA, dc_CR, dc_Rl).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property patient_prop1 = new Property(PropertyType.HAS, Rl, 0.001, pDj);
	private static Property patient_prop2 = new Property(PropertyType.NEGATION, patient_prop1);
	private static Property patient_prop3 = new Property(PropertyType.HAS, CR, 0.001, pDj);
	private static Property patient_prop4 = new Property(PropertyType.NEGATION, patient_prop3);
	private static Property patient_prop5 = new Property(PropertyType.HAS, SA, 0.001, pDj);
	private static Property patient_prop6 = new Property(PropertyType.NEGATION, patient_prop5);
	private static Property patient_prop7 = new Property(PropertyType.CONJUNCTION, patient_prop2, patient_prop4);
	private static Property patient_prop8 = new Property(PropertyType.CONJUNCTION, patient_prop6, patient_prop7);
	private static Property patient_prop9 = new Property(PropertyType.HAS, Rl, 0.001, mDj);
	private static Property patient_prop10 = new Property(PropertyType.NEGATION, patient_prop9);
	//TODO more?
	private static Set<Property> pSet3 = Stream.of(patient_prop2, patient_prop4, patient_prop6, patient_prop8, patient_prop10).collect(Collectors.toCollection(LinkedHashSet::new));

	//########## Medical Research Register #################
	// Components
	private static Component MC = new Component("MC");
	//private static Component CR = new Component("CR");
	private static Component R1 = new Component("R1");
	private static Component R2 = new Component("R2");
	private static Component R3 = new Component("R3");
	private static Component HI = new Component("HI");
	private static Set<Component> cSet4 = Stream.of(MC, CR, R1, R2, R3, HI).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable emD = new Variable("emD");
	private static Variable pmD = new Variable("pmD");
	private static Variable bp = new Variable("bloodpressure");
	private static Variable BP = new Variable("BP");
	private static Variable bcc = new Variable("bloodcellcount");
	private static Variable BCC = new Variable("BCC");
	private static Variable cl = new Variable("cholesterollevel");
	private static Variable CL = new Variable("CL");
	//private static Variable stats = new Variable("stats");
	private static Variable ed = new Variable("erectileDysfunction");
	private static Variable ED = new Variable("ED");
	private static Variable dp = new Variable("depression");
	private static Variable DP = new Variable("DP");
	private static Set<Variable> vSet4 = Stream.of(emD, pmD, bp, BP, bcc, BCC, cl, CL, stats, ed, ED, dp, DP).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termEmD = new Term(TermType.ATOM, emD, false);
	private static Term termPmD = new Term(TermType.ATOM, pmD, false);
	private static Term termBp = new Term(TermType.ATOM, bp, false);
	private static Term termBP = new Term(TermType.ATOM, BP, false);
	private static Term termBcc = new Term(TermType.ATOM, bcc, false);
	private static Term termBCC = new Term(TermType.ATOM, BCC, false);
	private static Term termCl = new Term(TermType.ATOM, cl, false);
	private static Term termCL = new Term(TermType.ATOM, CL, false);
	//private static Term termStats = new Term(TermType.ATOM, stats, false);
	private static Term termEd = new Term(TermType.ATOM, ed, false);
	private static Term termED = new Term(TermType.ATOM, ED, false);
	private static Term termDp = new Term(TermType.ATOM, dp, false);
	private static Term termDP = new Term(TermType.ATOM, DP, false);
	private static Term termDemD = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "dec", termEmD, false);
	private static Term termPdemD = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "pseudo", termDemD, false);
	private static Term termApmD = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "aggregate", termPmD, false);
	private static Term termEbp = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "extract", termPmD, termBP, false);
	private static Term termEbcc = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "extract", termPmD, termBCC, false);
	private static Term termEcl = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "extract", termPmD, termCL, false);
	private static Term termEed = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "extract", termPmD, termED, false);
	private static Term termEdp = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "extract", termPmD, termDP, false);
	private static Set<Term> tSet4 = Stream.of(
			termEmD, termPmD, termBp, termBP, termBcc, termBCC, termCl, termCL, termStats, termEd,
			termED, termDp, termDP, termDemD, termPdemD, termApmD, termEbp, termEcl, termEbcc, termEed,
			termEdp).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation pmD_pseudo = new Equation(
			"pmD_pseudo", Type.RELATION, Relation.EQUALITY, termPmD, termPdemD);
	private static Equation bp_extract = new Equation(
			"bp_extract", Type.RELATION, Relation.EQUALITY, termBp, termEbp);
	private static Equation bcc_extract = new Equation(
			"bcc_extract", Type.RELATION, Relation.EQUALITY, termBcc, termEbcc);
	private static Equation cl_extract = new Equation(
			"cl_extract", Type.RELATION, Relation.EQUALITY, termCl, termEcl);
	private static Equation ed_extract = new Equation(
			"ed_extract", Type.RELATION, Relation.EQUALITY, termEd, termEed);
	private static Equation dp_extract = new Equation(
			"dp_extract", Type.RELATION, Relation.EQUALITY, termDp, termEdp);
	private static Equation stats_aggregate = new Equation(
			"stats_aggregate", Type.RELATION, Relation.EQUALITY, termStats, termApmD);
	private static Set<Equation> eSet4 = Stream.of(
			pmD_pseudo, bp_extract, bcc_extract, cl_extract, ed_extract, dp_extract, stats_aggregate).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet4 = new LinkedHashSet<Trust>();
	// Statements
	private static Set<architecture.Statement> stSet4 = new LinkedHashSet<architecture.Statement>();
	// Purposes
	private static Purpose medRes = new Purpose("MedicalResearch", Stream.of(emD).collect(Collectors.toCollection(LinkedHashSet::new)));
	private static Purpose cvRes = new Purpose("CardioVascResearch", Stream.of(emD).collect(Collectors.toCollection(LinkedHashSet::new)));
	private static Purpose vRes = new Purpose("VascResearch", Stream.of(bp, bcc).collect(Collectors.toCollection(LinkedHashSet::new)));
	private static Purpose cRes = new Purpose("CardResearch", Stream.of(bp, cl).collect(Collectors.toCollection(LinkedHashSet::new)));
	private static Purpose qolRes = new Purpose("QoLResearch", Stream.of(dp, ed).collect(Collectors.toCollection(LinkedHashSet::new)));
	private static Purpose Profiling = new Purpose("Profiling", Stream.of(pmD).collect(Collectors.toCollection(LinkedHashSet::new)));
	private static List<Purpose> puList = new ArrayList<Purpose>(List.of(medRes, cvRes, cRes, vRes, qolRes, Profiling));
	private static Set<Purpose> puSet4 = new LinkedHashSet<Purpose>(Set.of(medRes, cvRes, cRes, vRes, qolRes, Profiling));
	private static boolean [][] am = {{false, true, false, false, true, false}, {false, false, true, true, false, false},
			{false, false, false, false, false, false}, {false, false, false, false, false, false},
			{false, false, false, false, false, false}, {false, false, false, false, false, false}}; //TODO also include top and bot?
	private static PurposeHierarchy purpHier = new PurposeHierarchy(puList, am);
	// Actions
	private static Action hasMC_emD = new Action(ActionType.HAS, MC, emD);
	private static Action hasCR_BP = new Action(ActionType.HAS, CR, BP);
	private static Action hasCR_BCC = new Action(ActionType.HAS, CR, BCC);
	private static Action hasCR_CL = new Action(ActionType.HAS, CR, CL);
	private static Action hasCR_ED = new Action(ActionType.HAS, CR, ED);
	private static Action hasCR_DP = new Action(ActionType.HAS, CR, DP);
	private static Action computeCR_pmD = new Action(ActionType.COMPUTE, CR, pmD_pseudo);
	private static Action computeCR_bp = new Action(ActionType.COMPUTE, CR, bp_extract);
	private static Action computeCR_bcc = new Action(ActionType.COMPUTE, CR, bcc_extract);
	private static Action computeCR_cl = new Action(ActionType.COMPUTE, CR, cl_extract);
	private static Action computeCR_ed = new Action(ActionType.COMPUTE, CR, ed_extract);
	private static Action computeCR_dp = new Action(ActionType.COMPUTE, CR, dp_extract);
	private static Action computeCR_stats = new Action(ActionType.COMPUTE, CR, stats_aggregate);
	private static Action preceiveCRMC = new Action(ActionType.PRECEIVE, CR, MC, cvRes, Set.of(emD));
	private static Action preceiveR1CR = new Action(ActionType.PRECEIVE, R1, CR, vRes, Set.of(bp, bcc));
	private static Action preceiveR2CR = new Action(ActionType.PRECEIVE, R2, CR, cRes, Set.of(bp, cl));
	private static Action preceiveR3CR = new Action(ActionType.PRECEIVE, R3, CR, qolRes, Set.of(ed, dp));
	private static Action preceiveHICR = new Action(ActionType.PRECEIVE, HI, CR, Profiling, Set.of(stats));
	private static Set<Action> aSet4 = Stream.of(
			hasMC_emD, hasCR_BP, hasCR_BCC, hasCR_CL, hasCR_ED, hasCR_DP, computeCR_pmD, computeCR_bp,
			computeCR_bcc, computeCR_cl, computeCR_ed, computeCR_dp, computeCR_stats, preceiveCRMC,
			preceiveR1CR, preceiveR2CR, preceiveR3CR, preceiveHICR).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	//TODO
	private static Dep mrr_dep1 = new Dep(pmD, Set.of(emD), 1);
	private static Dep mrr_dep2 = new Dep(bp, Set.of(pmD), 1);
	private static Dep mrr_dep3 = new Dep(bcc, Set.of(pmD), 1);
	private static Dep mrr_dep4 = new Dep(cl, Set.of(pmD), 1);
	private static Dep mrr_dep5 = new Dep(ed, Set.of(pmD), 1);
	private static Dep mrr_dep6 = new Dep(dp, Set.of(pmD), 1);
	private static Dep mrr_dep7 = new Dep(stats, Set.of(pmD), 1);
	private static Set<DependenceRelation> dSet4 = Stream.of(
			new DependenceRelation(MC, mrr_dep1), new DependenceRelation(MC, mrr_dep2),
			new DependenceRelation(MC, mrr_dep3), new DependenceRelation(MC, mrr_dep4),
			new DependenceRelation(MC, mrr_dep5), new DependenceRelation(MC, mrr_dep6),
			new DependenceRelation(MC, mrr_dep7), new DependenceRelation(CR, mrr_dep1),
			new DependenceRelation(CR, mrr_dep2), new DependenceRelation(CR, mrr_dep3),
			new DependenceRelation(CR, mrr_dep4), new DependenceRelation(CR, mrr_dep5),
			new DependenceRelation(CR, mrr_dep6), new DependenceRelation(CR, mrr_dep7)).collect(Collectors.toCollection(LinkedHashSet::new));
	//private static Set<DependenceRelation> dSet4 = new LinkedHashSet<DependenceRelation>();
	// Deductions
	private static DeductionCapability dc_MC = new DeductionCapability(MC, Set.of(deduc4));
	//private static DeductionCapability dc_CR = new DeductionCapability(CR, Set.of(deduc4));
	private static DeductionCapability dc_R1 = new DeductionCapability(R1, Set.of(deduc4));
	private static DeductionCapability dc_R2 = new DeductionCapability(R2, Set.of(deduc4));
	private static DeductionCapability dc_R3 = new DeductionCapability(R3, Set.of(deduc4));
	private static DeductionCapability dc_HI = new DeductionCapability(HI, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet4 = Stream.of(dc_MC, dc_R1, dc_R2, dc_R3, dc_CR, dc_HI).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property res_prop1 = new Property(PropertyType.NOTPURP, CR);
	private static Property res_prop2 = new Property(PropertyType.NOTPURP, MC);
	//TODO more?
	private static Set<Property> pSet4 = Stream.of(res_prop1, res_prop2).collect(Collectors.toCollection(LinkedHashSet::new));

	//############## AccuWeather ###############
	// Components
	private static Component US = new Component("US");
	private static Component SP = new Component("SP");
	private static Component AP = new Component("AP");
	private static Component SV = new Component("SV");
	private static Set<Component> cSet5 = Stream.of(US, SP, AP, RM, SV).collect(Collectors.toCollection(LinkedHashSet::new));
	// Variables
	private static Variable gps = new Variable("gps");
	private static Variable long_lat = new Variable("long_lat");
	private static Variable aloc = new Variable("aloc");
	private static Variable lbs = new Variable("lbs");
	private static Set<Variable> vSet5 = Stream.of(wifi_info, gps, long_lat, aloc, lbs).collect(Collectors.toCollection(LinkedHashSet::new));
	// Terms
	private static Term termGps = new Term(TermType.ATOM, gps, false);
	private static Term termLong_lat = new Term(TermType.ATOM, long_lat, false);
	private static Term termAloc = new Term(TermType.ATOM, aloc, false);
	private static Term termLbs = new Term(TermType.ATOM, lbs, false);
	private static Term termLocate = new Term(
			TermType.COMPOSITION, OperatorType.BINARY, Operator.FUNC, "locate", termGps, termWifi_info, false);
	private static Term termApprox = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "approx", termWifi_info, false);
	private static Term termService = new Term(
			TermType.COMPOSITION, OperatorType.UNARY, Operator.FUNC, "service", termLong_lat, false);
	private static Set<Term> tSet5 = Stream.of(
			termLocation, termWifi_info, termGps, termLong_lat, termAloc, termLbs, termLocate, termApprox, termService).collect(Collectors.toCollection(LinkedHashSet::new));
	// Equations
	private static Equation locate = new Equation(
			"locate", Type.RELATION, Relation.EQUALITY, termLong_lat, termLocate);
	private static Equation approx = new Equation(
			"approx", Type.RELATION, Relation.EQUALITY, termAloc, termApprox);
	private static Equation service = new Equation(
			"service", Type.RELATION, Relation.EQUALITY, termLbs, termService);
	private static Set<Equation> eSet5 = Stream.of(locate, approx, service).collect(Collectors.toCollection(LinkedHashSet::new));
	// Trusts
	private static Set<Trust> trustSet5 = new LinkedHashSet<Trust>();
	// Statements
	private static Set<architecture.Statement> stSet5 = new LinkedHashSet<architecture.Statement>();
	// Data Types
	private static DataType dt = new DataType("location", Set.of(gps, wifi_info, long_lat, aloc));
	private static DataType dt_new1 = new DataType("location", Set.of(gps, long_lat, aloc));
	private static DataType dt_new2 = new DataType("network", Set.of(wifi_info));
	private static Set<DataType> dtSet = Stream.of(dt).collect(Collectors.toCollection(LinkedHashSet::new));
	private static Set<DataType> dtSet6 = Stream.of(dt_new1, dt_new2).collect(Collectors.toCollection(LinkedHashSet::new));
	// Actions
	private static Action permission = new Action(ActionType.PERMISSION, SP, US, dt);
	private static Action revoke = new Action(ActionType.REVOKE, SP, US, dt);
	private static Action permission1 = new Action(ActionType.PERMISSION, SP, US, dt_new1);
	private static Action revoke1 = new Action(ActionType.REVOKE, SP, US, dt_new1);
	private static Action permission2 = new Action(ActionType.PERMISSION, SP, US, dt_new2);
	private static Action revoke2 = new Action(ActionType.REVOKE, SP, US, dt_new2);
	private static Action hasSP_gps = new Action(ActionType.HAS, SP, gps);
	private static Action hasSP_wifi_info = new Action(ActionType.HAS, SP, wifi_info);
	private static Action computeSP_long_lat = new Action(
			ActionType.COMPUTE, SP, locate);
	private static Action computeSV_lbs = new Action(
			ActionType.COMPUTE, SV, service);
	private static Action computeRM_aloc = new Action(
			ActionType.COMPUTE, RM, approx);
	private static Action receiveAPSP = new Action(
			ActionType.RECEIVE, AP, SP, Collections.emptySet(), Set.of(wifi_info));
	private static Action receiveRMAP = new Action(
			ActionType.RECEIVE, RM, AP, Collections.emptySet(), Set.of(wifi_info));
	private static Action creceiveAPSP2 = new Action(
			ActionType.CRECEIVE, AP, SP, dt_new2, Set.of(wifi_info));
	private static Action creceiveRMAP2 = new Action(
			ActionType.CRECEIVE, RM, AP, dt_new2, Set.of(wifi_info));
	private static Action creceiveAPSP1 = new Action(
			ActionType.CRECEIVE, AP, SP, dt_new1, Set.of(long_lat));
	private static Action creceiveSVAP1 = new Action(
			ActionType.CRECEIVE, SV, AP, dt_new1, Set.of(long_lat));
	private static Action creceiveRMAP1 = new Action(
			ActionType.CRECEIVE, RM, AP, dt_new1, Set.of(long_lat));
	private static Action creceiveAPSP = new Action(
			ActionType.CRECEIVE, AP, SP, dt, Set.of(long_lat));
	private static Action creceiveSVAP = new Action(
			ActionType.CRECEIVE, SV, AP, dt, Set.of(long_lat));
	private static Action creceiveRMAP = new Action(
			ActionType.CRECEIVE, RM, AP, dt, Set.of(long_lat));
	private static Set<Action> aSet5 = Stream.of(
			permission, revoke, hasSP_gps, hasSP_wifi_info, computeSP_long_lat, computeSV_lbs, computeRM_aloc, receiveAPSP, receiveRMAP, creceiveAPSP, creceiveSVAP, creceiveRMAP).collect(Collectors.toCollection(LinkedHashSet::new));
	private static Set<Action> aSet6 = Stream.of(
			permission1, revoke1, permission2, revoke2, hasSP_gps, hasSP_wifi_info, computeSP_long_lat, computeSV_lbs, computeRM_aloc, creceiveAPSP2, creceiveRMAP2, creceiveAPSP1, creceiveSVAP1, creceiveRMAP1).collect(Collectors.toCollection(LinkedHashSet::new));
	// Dependencies
	private static Set<DependenceRelation> dSet5 = new LinkedHashSet<DependenceRelation>();
	// Deductions
	private static DeductionCapability dc_US = new DeductionCapability(US, Set.of(deduc4));
	private static DeductionCapability dc_SP = new DeductionCapability(SP, Set.of(deduc4));
	private static DeductionCapability dc_AP = new DeductionCapability(AP, Set.of(deduc4));
	private static DeductionCapability dc_SV = new DeductionCapability(SV, Set.of(deduc4));
	private static Set<DeductionCapability> dedSet5 = Stream.of(dc_US, dc_SP, dc_RM, dc_AP, dc_SV).collect(Collectors.toCollection(LinkedHashSet::new));
	// Statements
	private static Property property_consent = new Property(PropertyType.CONSENTVIOLATED, SP, dt);
	//TODO 1 more?
	private static Set<Property> pSet5 = Stream.of(property_consent).collect(Collectors.toCollection(LinkedHashSet::new));


	/**
	 * Method to load one of the case studies.
	 * 
	 * @param archFunc
	 * 			the architecture functions object that holds all information about the architecture
	 * @param example
	 * 			the case study to load
	 */
	public static void load(ArchitectureFunctions archFunc, CaseStudy example) {
		// set all the necessary list of the right architecture
		switch (example) {
		case SEM:
			// first case study: smart energy metering
			archFunc.setcSet(cSet1);
			archFunc.setvSet(vSet1);
			archFunc.settSet(tSet1);
			archFunc.seteSet(eSet1);
			archFunc.settrustSet(trustSet1);
			archFunc.setstSet(stSet1);
			archFunc.setaSet(aSet1);
			archFunc.setdSet(dSet1);
			archFunc.setdedSet(dedSet1);
			archFunc.setpSet(pSet1);
			archFunc.setcomposSet(composSet1);
			break;
		case AW:
			// second case study: accuweather ios app
			archFunc.setcSet(cSet2);
			archFunc.setvSet(vSet2);
			archFunc.settSet(tSet2);
			archFunc.seteSet(eSet2);
			archFunc.settrustSet(trustSet2);
			archFunc.setstSet(stSet2);
			archFunc.setaSet(aSet2);
			archFunc.setdSet(dSet2);
			archFunc.setdedSet(dedSet2);
			archFunc.setpSet(pSet2);
			archFunc.setcomposSet(composSet2);
			break;
		case PDR:
			// third case study: patient data register
			archFunc.setcSet(cSet3);
			archFunc.setvSet(vSet3);
			archFunc.settSet(tSet3);
			archFunc.seteSet(eSet3);
			archFunc.settrustSet(trustSet3);
			archFunc.setstSet(stSet3);
			archFunc.setaSet(aSet3);
			archFunc.setdSet(dSet3);
			archFunc.setdedSet(dedSet3);
			archFunc.setpSet(pSet3);
			archFunc.setcomposSet(composSet3);
			break;
		case MRR:
			// fourth case study: medical research register
			archFunc.setcSet(cSet4);
			archFunc.setvSet(vSet4);
			archFunc.settSet(tSet4);
			archFunc.seteSet(eSet4);
			archFunc.settrustSet(trustSet4);
			archFunc.setstSet(stSet4);
			archFunc.setaSet(aSet4);
			archFunc.setdSet(dSet4);
			archFunc.setdedSet(dedSet4);
			archFunc.setpSet(pSet4);
			archFunc.setpuSet(puSet4);
			archFunc.setPurpHier(purpHier);
			break;
		case DPIA:
			// fifth case study: AccuWeather Data Protection Impact Assessment
			archFunc.setcSet(cSet5);
			archFunc.setvSet(vSet5);
			archFunc.settSet(tSet5);
			archFunc.seteSet(eSet5);
			archFunc.settrustSet(trustSet5);
			archFunc.setstSet(stSet5);
			archFunc.setaSet(aSet5);
			archFunc.setdSet(dSet5);
			archFunc.setdedSet(dedSet5);
			archFunc.setpSet(pSet5);
			archFunc.setdtSet(dtSet);
			break;
		case DPIA2:
			// fifth case study: AccuWeather Data Protection Impact Assessment
			archFunc.setcSet(cSet5);
			archFunc.setvSet(vSet5);
			archFunc.settSet(tSet5);
			archFunc.seteSet(eSet5);
			archFunc.settrustSet(trustSet5);
			archFunc.setstSet(stSet5);
			archFunc.setaSet(aSet6);
			archFunc.setdSet(dSet5);
			archFunc.setdedSet(dedSet5);
			archFunc.setpSet(pSet5);
			archFunc.setdtSet(dtSet6);
			break;
		default:
			break;
		}
	}

}
