package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import architecture.Action;
import architecture.Architecture;
import architecture.Attest;
import architecture.Component;
import architecture.Composition;
import architecture.DataType;
import architecture.Deduction;
import architecture.DeductionCapability;
import architecture.DependenceRelation;
import architecture.Equation;
import architecture.Statement;
import architecture.Term;
import architecture.Trust;
import architecture.Variable;
import architecture.Equation.Type;
import architecture.Proof;
import architecture.Purpose;
import architecture.PurposeHierarchy;
import architecture.Term.Operator;
import architecture.Term.OperatorType;
import diagrams.ComponentFigure;
import gui.ArchitectureFunctions.CaseStudy;
import properties.Property;
import properties.Property.PropertyType;
import utils.FileReader;
import utils.SaveLoadArch;

/**
 * GUI object that opens a shell and displays all necessary composites.
 * Also updates all tables, etc with the ArchitectureFunctions lists.
 */
public class Gui {

	/**
	 * Flag to indicate the OS.
	 * As SWT displays dropdowns differently on Mac and Windows, 
	 * this flag has to be set for the GUI to work on os X.
	 * If this flag is set for windows, the GUI still works but very inefficiently.
	 */
	public final static boolean isMac = false;

	/**
	 * The types of message boxes.
	 * {@link #INF INF} displays an info box that can only be acknowledged
	 * {@link #ERR ERR} displays an error message that leads to a reset
	 * {@link #LOG LOG} displays the trace of a verification
	 * {@link #WARN WARN} displays a warning that informs of a reset
	 */
	public static enum MessageType {
		INF, ERR, LOG, WARN;
	}

	/**
	 * The different types of objects, like components, variables, or properties.
	 */
	private enum ObjectType {
		COMP, VAR, TERM, EQ, TRUST, STMT, ACT, DEP, DED, PROP, PURP, DT;
	}

	// class fields
	private static ArchitectureFunctions archFunc = new ArchitectureFunctions();
	public static Display display = new Display();
	public Shell shell = new Shell(display);

	/**
	 * The constructor of the GUI that initializes the shell and its content,
	 * displays the GUI and also contains the program loop.
	 */
	public Gui() {
		shell.setText("CAPVerDE v1.15: Computer-Aided Privacy Verification and Design Engineering Tool");
		shell.setLayout(new FillLayout());
		// shell.setLayout(new GridLayout(2, true));

		// event listener
		final MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				Text t = (Text) e.widget;
				t.selectAll();
			}
		};

		// ################################## tabs
		// ########################################
		// scrollable parent
		ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayout(new FillLayout(SWT.HORIZONTAL));
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		Composite everything = new Composite(sc, SWT.BORDER);
		everything.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		everything.setLayout(new GridLayout(1, false));
		everything.setBackground(display.getSystemColor(SWT.COLOR_GRAY));

		// the top line for all extra options like save,load,edit,finish,draw
		Composite top = new Composite(everything, SWT.BORDER);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		top.setLayout(new GridLayout(7, false));
		top.setBackground(display.getSystemColor(SWT.COLOR_GRAY));

		// finish
		Group finish = new Group(top, SWT.SHADOW_IN);
		finish.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		finish.setLayout(new GridLayout(1, false));
		finish.setText("Finish Architecture Creation");

		Button finishButton = new Button(finish, SWT.PUSH);
		finishButton.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		finishButton.setText("Finish");
		finishButton.setToolTipText(
				"The architecture will be considered created and will be verified for consistency.");
		finishButton.addListener(SWT.Selection, event -> archFunc.finish());
		finishButton.addListener(SWT.Selection, event -> finishButton.setEnabled(false));

		// load
		Group load = new Group(top, SWT.SHADOW_IN);
		load.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		load.setLayout(new GridLayout(2, false));
		load.setText("Load Example Architectures");

		Combo examples = new Combo(load, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			//examples = new Combo(load, SWT.SIMPLE | SWT.READ_ONLY);
		}
		examples.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		examples.setText("Example Case Studies");
		examples.add(CaseStudy.SEM.toString());
		examples.add(CaseStudy.AW.toString());
		examples.add(CaseStudy.PDR.toString());
		examples.add(CaseStudy.MRR.toString());
		examples.add(CaseStudy.DPIA.toString());
		examples.add(CaseStudy.DPIA2.toString());
		if (!isMac) {
			// examples.addListener(SWT.DROP_DOWN, event -> updateCaseStudies(archFunc, examples));
		}

		Button loadButton = new Button(load, SWT.PUSH);
		loadButton.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		loadButton.setText("Load");
		loadButton.setToolTipText(
				"Instead of modeling a new architecture, one of the example case studies can be loaded.");
		/*
    loadButton.addListener(SWT.Selection, event -> archFunc.load(examples.getText()));
    loadButton.addListener(SWT.Selection, event -> archFunc.finish());
    loadButton.addListener(SWT.Selection, event -> loadButton.setEnabled(false));
    loadButton.addListener(SWT.Selection, event -> finishButton.setEnabled(false));*/
		//finishButton.addListener(SWT.Selection, event -> loadButton.setEnabled(false));

		// save/load
		Group saveload = new Group(top, SWT.SHADOW_IN);
		saveload.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		saveload.setLayout(new GridLayout(5, false));
		saveload.setText("Save/Load Architectures");

		Text archName = new Text(saveload, SWT.SINGLE);
		archName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		archName.setText("name");
		archName.setToolTipText("The name of the architecture to save or load");
		archName.addMouseListener(mouseListener);

		Button saveButton = new Button(saveload, SWT.PUSH);
		saveButton.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		saveButton.setText("Save");
		saveButton.setToolTipText("Save a created architecture to disk.");
		//saveButton.addListener(SWT.Selection, event -> archFunc.save2file(archName.getText()));

		Button loadButton2 = new Button(saveload, SWT.PUSH);
		loadButton2.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		loadButton2.setText("Load");
		loadButton2.setToolTipText("Load a previously saved architecture from disk.");
		/*
    loadButton.addListener(SWT.Selection, event -> saveButton.setEnabled(false));
    loadButton.addListener(SWT.Selection, event -> loadButton2.setEnabled(false));*/

		// edit
		Group reset = new Group(top, SWT.SHADOW_IN);
		reset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		reset.setLayout(new GridLayout(2, false));
		reset.setText("Remodeling of Architecture");

		Button editBtn = new Button(reset, SWT.PUSH);
		editBtn.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		editBtn.setText("Edit");
		editBtn.setToolTipText("Go back to architecture modeling.");
		// editBtn.addListener(SWT.Selection, event -> left.setEnabled(true));
		// //folder.getTabList()[0]
		// editBtn.addListener(SWT.Selection, event -> left2.setEnabled(false));
		// //folder.getTabList()[2]
		editBtn.addListener(SWT.Selection, event -> finishButton.setEnabled(true));
		editBtn.addListener(SWT.Selection, event -> loadButton.setEnabled(true));
		editBtn.addListener(SWT.Selection, event -> saveButton.setEnabled(true));
		editBtn.addListener(SWT.Selection, event -> loadButton2.setEnabled(true));

		Button resetBtn = new Button(reset, SWT.PUSH);
		resetBtn.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		resetBtn.setText("Reset");
		resetBtn.setToolTipText("Reset the architecture and start from scratch.");

		// draw
		Group diagram = new Group(top, SWT.SHADOW_IN);
		diagram.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		diagram.setLayout(new GridLayout(1, false));
		diagram.setText("Architecture Diagram");

		Button drawButton = new Button(diagram, SWT.PUSH);
		drawButton.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		drawButton.setText("Draw");
		drawButton.setToolTipText("Show a graphical representation of the architecture.");
		drawButton.addListener(SWT.Selection, event -> showDiagram());
		drawButton.setEnabled(false);
		editBtn.addListener(SWT.Selection, event -> drawButton.setEnabled(false));
		finishButton.addListener(SWT.Selection, event -> drawButton.setEnabled(true));

		// show purpose hierarchy
		Group purpHier = new Group(top, SWT.SHADOW_IN);
		purpHier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		purpHier.setLayout(new GridLayout(1, false));
		purpHier.setText("Purpose Hierarchy");

		Button hierButton = new Button(purpHier, SWT.PUSH);
		hierButton.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		hierButton.setText("Show");
		hierButton.setToolTipText("Show the purpose hierarchy as lattice.");
		hierButton.addListener(SWT.Selection, event -> showHierarchy());

		// impact assessment
		Group DPIA = new Group(top, SWT.SHADOW_IN);
		DPIA.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		DPIA.setLayout(new GridLayout(1, false));
		DPIA.setText("Data Protection Impact Assessment");

		Button startIA = new Button(DPIA, SWT.PUSH);
		startIA.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		startIA.setText("Start");
		startIA.setToolTipText("Starts the Data Protection Impact Assessment.");
		startIA.addListener(SWT.Selection, event -> impactAssessment());
		startIA.setEnabled(false);
		editBtn.addListener(SWT.Selection, event -> startIA.setEnabled(false));
		finishButton.addListener(SWT.Selection, event -> startIA.setEnabled(true));

		// tab folder
		TabFolder folder = new TabFolder(everything, SWT.NONE);
		folder.setLayout(new GridLayout(1, false));

		finishButton.addListener(SWT.Selection, event -> folder.setSelection(1));

		// first tab
		TabItem tab1 = new TabItem(folder, SWT.NONE);
		tab1.setText("Architecture Design");

		// left and right sides
		Composite bothSides1 = new Composite(folder, SWT.NONE);
		bothSides1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		bothSides1.setLayout(new GridLayout(2, true));
		Composite left = new Composite(bothSides1, SWT.BORDER);
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		left.setLayout(new GridLayout(1, true));
		left.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		Composite right = new Composite(bothSides1, SWT.BORDER);
		right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		right.setLayout(new GridLayout(1, true));

		// left side
		// Components
		Group components = new Group(left, SWT.SHADOW_IN);
		components.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		components.setLayout(new GridLayout(10, true));
		components.setText("Components");

		Text compName = new Text(components, SWT.SINGLE);
		compName.setText("name");
		compName.setToolTipText("The name of the component to add");
		compName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
		compName.addMouseListener(mouseListener);

		Button compAdd = new Button(components, SWT.PUSH);
		compAdd.setText("Add");
		compAdd.addListener(SWT.Selection, event -> archFunc.addComponent(compName.getText()));

		// Variables
		Group variables = new Group(left, SWT.SHADOW_IN);
		variables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		variables.setLayout(new GridLayout(5, true));
		variables.setText("Variables");

		Text varName = new Text(variables, SWT.SINGLE);
		varName.setText("name");
		varName.setToolTipText("The name of the variable to add");
		varName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
		varName.addMouseListener(mouseListener);

		Button varAdd = new Button(variables, SWT.PUSH);
		varAdd.setText("Add");
		varAdd.addListener(SWT.Selection, event -> archFunc.addVariable(varName.getText()));

		// Terms
		Group subterms = new Group(left, SWT.SHADOW_IN);
		subterms.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		subterms.setLayout(new GridLayout(31, false));
		subterms.setText("Sub-Terms");

		Group opType = new Group(subterms, SWT.SHADOW_ETCHED_OUT);
		opType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		opType.setLayout(new GridLayout(3, true));
		opType.setText("Type of operator");
		Button unary = new Button(opType, SWT.RADIO);
		unary.setText("unary");
		unary.setSelection(true);
		Button binary = new Button(opType, SWT.RADIO);
		binary.setText("binary");
		Button tertiary = new Button(opType, SWT.RADIO);
		tertiary.setText("tertiary");

		Combo operator; // = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			operator = new Combo(subterms, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			operator = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		operator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		operator.setText("Operator");
		operator.add("FUNC");
		//operator.add("ADD");
		//operator.add("MULT");
		//operator.add("SUB");
		//operator.add("DIV");
		operator.select(0);
		//operator.addListener(SWT.DROP_DOWN, event -> updateOperators(operator, binary));
		unary.addListener(SWT.Selection, event -> updateOperators(operator, binary));
		binary.addListener(SWT.Selection, event -> updateOperators(operator, binary));
		tertiary.addListener(SWT.Selection, event -> updateOperators(operator, binary));

		Text funcName = new Text(subterms, SWT.SINGLE);
		funcName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		funcName.setText("function name");
		funcName.setToolTipText("The name of the custom function");
		funcName.setEnabled(true);
		/*operator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (operator.getText().equals("FUNC")) {
					funcName.setEnabled(true);
				} else {
					funcName.setEnabled(false);
				}
			}
		});*/
		funcName.addMouseListener(mouseListener);

		Combo term1;// = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			term1 = new Combo(subterms, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			term1 = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		term1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 2));
		term1.setText("first Term");
		term1.setToolTipText("first Term");
		if (!isMac) {
			term1.addListener(SWT.DROP_DOWN, event -> updateTerms(term1));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateTerms(term1));
		}

		Combo term2;// = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			term2 = new Combo(subterms, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			term2 = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		term2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 2));
		term2.setText("second Term");
		term2.setEnabled(false);
		if (!isMac) {
			term2.addListener(SWT.DROP_DOWN, event -> updateTerms(term2));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateTerms(term2));
		}
		unary.addListener(SWT.Selection, event -> term2.setEnabled(false));
		binary.addListener(SWT.Selection, event -> term2.setEnabled(true));
		tertiary.addListener(SWT.Selection, event -> term2.setEnabled(true));

		Combo term3;// = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			term3 = new Combo(subterms, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			term3 = new Combo(subterms, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		term3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		term3.setText("third Term");
		term3.setEnabled(false);
		if (!isMac) {
			term3.addListener(SWT.DROP_DOWN, event -> updateTerms(term3));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateTerms(term3));
		}
		unary.addListener(SWT.Selection, event -> term3.setEnabled(false));
		binary.addListener(SWT.Selection, event -> term3.setEnabled(false));
		tertiary.addListener(SWT.Selection, event -> term3.setEnabled(true));

		Button termAdd = new Button(subterms, SWT.PUSH);
		termAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		termAdd.setText("Add");
		termAdd.addListener(SWT.Selection, event
				-> handleTerms(unary, binary, operator, funcName, term1, term2, term3));
		if (isMac) {
			termAdd.addListener(SWT.Selection, event -> updateTerms(term1));
			termAdd.addListener(SWT.Selection, event -> updateTerms(term2));
			termAdd.addListener(SWT.Selection, event -> updateTerms(term3));
		}

		// Equations
		Group equations = new Group(left, SWT.SHADOW_IN);
		equations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		equations.setLayout(new GridLayout(12, false));
		equations.setText("Equations");

		Group eqType = new Group(equations, SWT.SHADOW_ETCHED_OUT);
		eqType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		eqType.setLayout(new GridLayout(2, true));
		eqType.setText("Type of equation");
		Button conjunc = new Button(eqType, SWT.RADIO);
		conjunc.setText("conjunction");
		Button equal = new Button(eqType, SWT.RADIO);
		equal.setText("equality");
		equal.setSelection(true);

		Text eqName = new Text(equations, SWT.SINGLE);
		eqName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		eqName.setText("equation name");
		eqName.setToolTipText("The name of the equation");
		eqName.addMouseListener(mouseListener);

		Combo eq1;// = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			eq1 = new Combo(equations, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			eq1 = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		eq1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		eq1.setText("first Equation");
		eq1.setEnabled(false);
		if (!isMac) {
			eq1.addListener(SWT.DROP_DOWN, event -> updateEquations(eq1));
		}
		conjunc.addListener(SWT.Selection, event -> eq1.setEnabled(true));
		equal.addListener(SWT.Selection, event -> eq1.setEnabled(false));

		Label and = new Label(equations, SWT.CENTER);
		and.setText("and");

		Combo eq2;// = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			eq2 = new Combo(equations, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			eq2 = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		eq2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		eq2.setText("second Equation");
		eq2.setEnabled(false);
		if (!isMac) {
			eq2.addListener(SWT.DROP_DOWN, event -> updateEquations(eq2));
		}
		conjunc.addListener(SWT.Selection, event -> eq2.setEnabled(true));
		equal.addListener(SWT.Selection, event -> eq2.setEnabled(false));

		Label comma = new Label(equations, SWT.CENTER);
		comma.setText(",");

		Combo t1;// = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			t1 = new Combo(equations, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			t1 = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		t1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		t1.setText("first Term");
		if (!isMac) {
			t1.addListener(SWT.DROP_DOWN, event -> updateTerms(t1));
		} else {
			termAdd.addListener(SWT.Selection, event -> updateTerms(t1));
			varAdd.addListener(SWT.Selection, event -> updateTerms(t1));
		}
		conjunc.addListener(SWT.Selection, event -> t1.setEnabled(false));
		equal.addListener(SWT.Selection, event -> t1.setEnabled(true));

		Label equals = new Label(equations, SWT.CENTER);
		equals.setText("=");

		Combo t2;// = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			t2 = new Combo(equations, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			t2 = new Combo(equations, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		t2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		t2.setText("second Term");
		if (!isMac) {
			t2.addListener(SWT.DROP_DOWN, event -> updateTerms(t2));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateTerms(t2));
			termAdd.addListener(SWT.Selection, event -> updateTerms(t2));
		}
		conjunc.addListener(SWT.Selection, event -> t2.setEnabled(false));
		equal.addListener(SWT.Selection, event -> t2.setEnabled(true));

		Button eqAdd = new Button(equations, SWT.PUSH);
		eqAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		eqAdd.setText("Add");
		eqAdd.addListener(SWT.Selection, event -> handleEquations(conjunc, eqName, eq1, eq2, t1, t2));
		if (isMac) {
			eqAdd.addListener(SWT.Selection, event -> updateEquations(eq1));
			eqAdd.addListener(SWT.Selection, event -> updateEquations(eq2));
		}

		// Statements
		Group statements = new Group(left, SWT.SHADOW_IN);
		statements.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		statements.setLayout(new GridLayout(27, false));
		statements.setText("Statements");

		Group stType = new Group(statements, SWT.SHADOW_ETCHED_OUT);
		stType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		stType.setLayout(new GridLayout(2, true));
		stType.setText("Type of statement");
		Button att = new Button(stType, SWT.RADIO);
		att.setText("attest");
		att.setSelection(true);
		Button pro = new Button(stType, SWT.RADIO);
		pro.setText("proof");

		Combo c3;// = new Combo(statements, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			c3 = new Combo(statements, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			c3 = new Combo(statements, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		c3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		c3.setText("Component");
		if (!isMac) {
			c3.addListener(SWT.DROP_DOWN, event -> updateComponents(c3));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(c3));
		}

		Label stLabel = new Label(statements, SWT.CENTER);
		stLabel.setText("attests/proves");

		Table eq3 = new Table(statements, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		eq3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		eq3.setToolTipText("Equations");
		// Debug
		eq3.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					System.out.println("You checked " + event.item);
				} else {
					System.out.println("You selected " + event.item);
					System.out.println(eq3.getSelectionIndices());
					// TableItem ti = (TableItem)event.item;
					// ti.setChecked(!ti.getChecked());
				}
			}
		});
		eqAdd.addListener(SWT.Selection, event -> updateEquationsTab(eq3));

		Label orLabel = new Label(statements, SWT.CENTER);
		orLabel.setText("or");

		Table att1 = new Table(statements, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		att1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		att1.setToolTipText("Attestations");
		att1.setEnabled(false);
		att.addListener(SWT.Selection, event -> att1.setEnabled(false));
		pro.addListener(SWT.Selection, event -> att1.setEnabled(true));

		Button stAdd = new Button(statements, SWT.PUSH);
		stAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		stAdd.setText("Add");
		stAdd.addListener(SWT.Selection, event -> handleStatement(att, c3, eq3, att1));
		stAdd.addListener(SWT.Selection, event -> updateAttestsTab(att1));

		// Purposes
		Group purposes = new Group(left, SWT.SHADOW_IN);
		purposes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		purposes.setLayout(new GridLayout(64, false));
		purposes.setText("Purposes");

		Text purpName = new Text(purposes, SWT.SINGLE);
		purpName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		purpName.setText("purpose name");
		purpName.setToolTipText("The name of the purpose");
		purpName.addMouseListener(mouseListener);

		Label withLabel = new Label(purposes, SWT.CENTER);
		withLabel.setText("with");

		Table vars1 = new Table(purposes, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		vars1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 10));
		vars1.setToolTipText("Variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(vars1));

		Label parentsLabel = new Label(purposes, SWT.CENTER);
		parentsLabel.setText("Parents:");

		Table purps1 = new Table(purposes, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		purps1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 20, 10));
		purps1.setToolTipText("Parent Purposes");

		Label childrenLabel = new Label(purposes, SWT.CENTER);
		childrenLabel.setText("Children:");

		Table purps2 = new Table(purposes, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		purps2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 20, 10));
		purps2.setToolTipText("Child Purposes");

		Button purpAdd = new Button(purposes, SWT.PUSH);
		purpAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		purpAdd.setText("Add");
		purpAdd.addListener(SWT.Selection, event -> handlePurpose(purpName, vars1, purps1, purps2));
		purpAdd.addListener(SWT.Selection, event -> updatePurpsTab(purps1));
		purpAdd.addListener(SWT.Selection, event -> updatePurpsTab(purps2));
		
		// Data Types
		Group dataTypes = new Group(left, SWT.SHADOW_IN);
		dataTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		dataTypes.setLayout(new GridLayout(15, false));
		dataTypes.setText("Data Types");

		Text dtName = new Text(dataTypes, SWT.SINGLE);
		dtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		dtName.setText("data type name");
		dtName.setToolTipText("The name of the data type");
		dtName.addMouseListener(mouseListener);

		Label encLabel = new Label(dataTypes, SWT.CENTER);
		encLabel.setText("encompasses");

		Table vars2 = new Table(dataTypes, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		vars2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 10));
		vars2.setToolTipText("Variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(vars2));

		Button dtAdd = new Button(dataTypes, SWT.PUSH);
		dtAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		dtAdd.setText("Add");
		dtAdd.addListener(SWT.Selection, event -> handleDt(dtName, vars2));

		// Trust
		Group trusts = new Group(left, SWT.SHADOW_IN);
		trusts.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		trusts.setLayout(new GridLayout(4, false));
		trusts.setText("Trust Relations");

		Combo c1;// = new Combo(trusts, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			c1 = new Combo(trusts, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			c1 = new Combo(trusts, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		c1.setText("Component1");
		if (!isMac) {
			c1.addListener(SWT.DROP_DOWN, event -> updateComponents(c1));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(c1));
		}

		Label trust = new Label(trusts, SWT.CENTER);
		trust.setText("blindly trusts");

		Combo c2;// = new Combo(trusts, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			c2 = new Combo(trusts, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			c2 = new Combo(trusts, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		c2.setText("Component2");
		if (!isMac) {
			c2.addListener(SWT.DROP_DOWN, event -> updateComponents(c2));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(c2));
		}

		Button trustAdd = new Button(trusts, SWT.PUSH);
		trustAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		trustAdd.setText("Add");
		trustAdd.addListener(SWT.Selection, event -> archFunc.addTrust(c1.getText(), c2.getText()));

		// Actions
		Group actions = new Group(left, SWT.SHADOW_IN);
		actions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		actions.setLayout(new GridLayout(1, false));
		actions.setText("Component Actions");

		// Has
		Group has = new Group(actions, SWT.SHADOW_IN);
		has.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		has.setLayout(new GridLayout(4, false));
		has.setText("Has");

		Combo comp;// = new Combo(has, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp = new Combo(has, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp = new Combo(has, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp.setText("Component");
		if (!isMac) {
			comp.addListener(SWT.DROP_DOWN, event -> updateComponents(comp));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp));
		}

		Label hasLab = new Label(has, SWT.CENTER);
		hasLab.setText("has");

		Combo var;// = new Combo(has, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			var = new Combo(has, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			var = new Combo(has, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		var.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		var.setText("Variable");
		if (!isMac) {
			var.addListener(SWT.DROP_DOWN, event -> updateVariables(var));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateVariables(var));
		}

		Button hasAdd = new Button(has, SWT.PUSH);
		hasAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		hasAdd.setText("Add");
		hasAdd.addListener(SWT.Selection, event -> archFunc.addHas(comp.getText(), var.getText()));

		// Compute
		Group compute = new Group(actions, SWT.SHADOW_IN);
		compute.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		compute.setLayout(new GridLayout(4, false));
		compute.setText("Compute");

		Combo comp1;// = new Combo(compute, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp1 = new Combo(compute, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp1 = new Combo(compute, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp1.setText("Component");
		if (!isMac) {
			comp1.addListener(SWT.DROP_DOWN, event -> updateComponents(comp1));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp1));
		}

		Label computeLab = new Label(compute, SWT.CENTER);
		computeLab.setText("computes");

		Combo eq;// = new Combo(compute, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			eq = new Combo(compute, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			eq = new Combo(compute, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		eq.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		eq.setText("new Equation");
		if (!isMac) {
			eq.addListener(SWT.DROP_DOWN, event -> updateEquations(eq));
		} else {
			eqAdd.addListener(SWT.Selection, event -> updateEquations(eq));
		}

		Button computeAdd = new Button(compute, SWT.PUSH);
		computeAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		computeAdd.setText("Add");
		computeAdd.addListener(
				SWT.Selection, event -> archFunc.addCompute(comp1.getText(), eq.getText()));

		// Receive
		Group receive = new Group(actions, SWT.SHADOW_IN);
		receive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		receive.setLayout(new GridLayout(75, false));
		receive.setText("Receive");

		Combo comp2;// = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp2 = new Combo(receive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp2 = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp2.setText("Component");
		if (!isMac) {
			comp2.addListener(SWT.DROP_DOWN, event -> updateComponents(comp2));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp2));
		}

		Label receiveLab = new Label(receive, SWT.CENTER);
		receiveLab.setText("receives");

		Table stTable = new Table(receive, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		stTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 40, 10));
		stTable.setToolTipText("Statements");
		stAdd.addListener(SWT.Selection, event -> updateStatementsTab(stTable));

		Label andLab = new Label(receive, SWT.CENTER);
		andLab.setText("and");

		Table varTable = new Table(receive, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		varTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 20, 10));
		varTable.setToolTipText("Variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(varTable));

		Label fromLab = new Label(receive, SWT.CENTER);
		fromLab.setText("from");

		Combo comp3;// = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp3 = new Combo(receive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp3 = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp3.setText("Component");
		if (!isMac) {
			comp3.addListener(SWT.DROP_DOWN, event -> updateComponents(comp3));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp3));
		}

		Button receiveAdd = new Button(receive, SWT.PUSH);
		receiveAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		receiveAdd.setText("Add");
		receiveAdd.addListener(SWT.Selection, event -> handleReceive(comp2, comp3, stTable, varTable));

		// Conditional Receive
		//TODO test
		Group creceive = new Group(actions, SWT.SHADOW_IN);
		creceive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		creceive.setLayout(new GridLayout(75, false));
		creceive.setText("CReceive");

		Combo comp15;// = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp15 = new Combo(creceive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp15 = new Combo(creceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp15.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp15.setText("Component");
		if (!isMac) {
			comp15.addListener(SWT.DROP_DOWN, event -> updateComponents(comp15));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp15));
		}

		Label receiveLab2 = new Label(creceive, SWT.CENTER);
		receiveLab2.setText("conditionally receives");

		Table varTable2 = new Table(creceive, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		varTable2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 40, 10));
		varTable2.setToolTipText("Variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(varTable2));

		Label underLab = new Label(creceive, SWT.CENTER);
		underLab.setText("under the condition of having consent for");

		Combo dt;// = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			dt = new Combo(creceive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			dt = new Combo(creceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		dt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		dt.setText("Data Type");
		if (!isMac) {
			dt.addListener(SWT.DROP_DOWN, event -> updateDataTypes(dt));
		} else {
			dt.addListener(SWT.Selection, event -> updateDataTypes(dt));
		}

		Label fromLab2 = new Label(creceive, SWT.CENTER);
		fromLab2.setText("from");

		Combo comp16;// = new Combo(receive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp16 = new Combo(creceive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp16 = new Combo(creceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp16.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp16.setText("Component");
		if (!isMac) {
			comp16.addListener(SWT.DROP_DOWN, event -> updateComponents(comp16));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp16));
		}

		Button creceiveAdd = new Button(creceive, SWT.PUSH);
		creceiveAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		creceiveAdd.setText("Add");
		creceiveAdd.addListener(SWT.Selection, event -> handleCReceive(comp15, comp16, dt, varTable2));

		// Purpose Receive
		Group preceive = new Group(actions, SWT.SHADOW_IN);
		preceive.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		preceive.setLayout(new GridLayout(75, false));
		preceive.setText("PReceive");

		Combo comp11;// = new Combo(preceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp11 = new Combo(preceive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp11 = new Combo(preceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp11.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp11.setText("Component");
		if (!isMac) {
			comp11.addListener(SWT.DROP_DOWN, event -> updateComponents(comp11));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp11));
		}

		Label preceiveLab = new Label(preceive, SWT.CENTER);
		preceiveLab.setText("receives");

		Combo purp1;// = new Combo(preceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			purp1 = new Combo(preceive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			purp1 = new Combo(preceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		purp1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		purp1.setText("Purpose");
		if (!isMac) {
			purp1.addListener(SWT.DROP_DOWN, event -> updatePurps(purp1));
		} else {
			purpAdd.addListener(SWT.Selection, event -> updatePurps(purp1));
		}

		Label andLab1 = new Label(preceive, SWT.CENTER);
		andLab1.setText("and");

		Table varTable1 = new Table(preceive, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		varTable1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 20, 10));
		varTable1.setToolTipText("Variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(varTable1));

		Label fromLab1 = new Label(preceive, SWT.CENTER);
		fromLab1.setText("from");

		Combo comp12;// = new Combo(preceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp12 = new Combo(preceive, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp12 = new Combo(preceive, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp12.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp12.setText("Component");
		if (!isMac) {
			comp12.addListener(SWT.DROP_DOWN, event -> updateComponents(comp12));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp12));
		}

		Button preceiveAdd = new Button(preceive, SWT.PUSH);
		preceiveAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		preceiveAdd.setText("Add");
		preceiveAdd.addListener(SWT.Selection, event -> handlePReceive(comp11, comp12, purp1, varTable1));

		// Check
		Group check = new Group(actions, SWT.SHADOW_IN);
		check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		check.setLayout(new GridLayout(150, false));
		check.setText("Check");

		Combo comp4;// = new Combo(check, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp4 = new Combo(check, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp4 = new Combo(check, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp4.setText("Component");
		if (!isMac) {
			comp4.addListener(SWT.DROP_DOWN, event -> updateComponents(comp4));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp4));
		}

		Label checksLab = new Label(check, SWT.CENTER);
		checksLab.setText("checks");

		Table eqTable = new Table(check, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		eqTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 40, 10));
		eqTable.setToolTipText("Equations");
		eqAdd.addListener(SWT.Selection, event -> updateEquationsTab(eqTable));

		Button checkAdd = new Button(check, SWT.PUSH);
		checkAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		checkAdd.setText("Add");
		checkAdd.addListener(SWT.Selection, event -> handleCheck(comp4, eqTable));

		// Verify
		Group verify = new Group(actions, SWT.SHADOW_IN);
		verify.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		verify.setLayout(new GridLayout(75, false));
		verify.setText("Verify");

		Group verifType = new Group(verify, SWT.SHADOW_ETCHED_OUT);
		verifType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		verifType.setLayout(new GridLayout(2, true));
		verifType.setText("Type of verification");
		Button attest = new Button(verifType, SWT.RADIO);
		attest.setText("attest");
		Button proof = new Button(verifType, SWT.RADIO);
		proof.setText("proof");
		proof.setSelection(true);

		Combo comp10;// = new Combo(verify, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp10 = new Combo(verify, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp10 = new Combo(verify, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp10.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp10.setText("Component");
		if (!isMac) {
			comp10.addListener(SWT.DROP_DOWN, event -> updateComponents(comp10));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp10));
		}

		Label verifLab = new Label(verify, SWT.CENTER);
		verifLab.setText("verifies");

		Combo proofs;// = new Combo(verify, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			proofs = new Combo(verify, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			proofs = new Combo(verify, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		proofs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		proofs.setText("Proofs");
		if (!isMac) {
			proofs.addListener(SWT.DROP_DOWN, event -> updateProofs(proofs));
		} else {
			stAdd.addListener(SWT.Selection, event -> updateProofs(proofs));
		}
		attest.addListener(SWT.Selection, event -> proofs.setEnabled(false));
		proof.addListener(SWT.Selection, event -> proofs.setEnabled(true));

		Label orLabel2 = new Label(verify, SWT.CENTER);
		orLabel2.setText("or");

		Combo attests;// = new Combo(verify, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			attests = new Combo(verify, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			attests = new Combo(verify, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		attests.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		attests.setText("Attestations");
		if (!isMac) {
			attests.addListener(SWT.DROP_DOWN, event -> updateAttests(attests));
		} else {
			stAdd.addListener(SWT.Selection, event -> updateAttests(attests));
		}
		attests.setEnabled(false);
		attest.addListener(SWT.Selection, event -> attests.setEnabled(true));
		proof.addListener(SWT.Selection, event -> attests.setEnabled(false));

		Button verifAdd = new Button(verify, SWT.PUSH);
		verifAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		verifAdd.setText("Add");
		verifAdd.addListener(SWT.Selection, event -> handleVerify(attest, comp10, proofs, attests));

		// Delete
		Group delete = new Group(actions, SWT.SHADOW_IN);
		delete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		delete.setLayout(new GridLayout(75, false));
		delete.setText("Delete");

		Combo comp7;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp7 = new Combo(delete, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp7 = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp7.setText("Component");
		if (!isMac) {
			comp7.addListener(SWT.DROP_DOWN, event -> updateComponents(comp7));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp7));
		}

		Label deleteLab = new Label(delete, SWT.CENTER);
		deleteLab.setText("deletes");

		Combo var1;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			var1 = new Combo(delete, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			var1 = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		var1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		var1.setText("Variable");
		if (!isMac) {
			var1.addListener(SWT.DROP_DOWN, event -> updateVariables(var1));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateVariables(var1));
		}

		Button deleteAdd = new Button(delete, SWT.PUSH);
		deleteAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		deleteAdd.setText("Add");
		deleteAdd.addListener(SWT.Selection, event -> archFunc.delete(comp7.getText(), var1.getText()));

		// Permission
		Group permission = new Group(actions, SWT.SHADOW_IN);
		permission.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		permission.setLayout(new GridLayout(75, false));
		permission.setText("Permission");

		Combo comp5;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp5 = new Combo(permission, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp5 = new Combo(permission, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp5.setText("Component");
		if (!isMac) {
			comp5.addListener(SWT.DROP_DOWN, event -> updateComponents(comp5));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp5));
		}

		Label receiveFromLab = new Label(permission, SWT.CENTER);
		receiveFromLab.setText("receives Permission from");

		Combo comp6;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp6 = new Combo(permission, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp6 = new Combo(permission, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp6.setText("Component");
		if (!isMac) {
			comp6.addListener(SWT.DROP_DOWN, event -> updateComponents(comp6));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp6));
		}

		Label withDtLab = new Label(permission, SWT.CENTER);
		withDtLab.setText("for the Data Type");

		Combo dt1;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			dt1 = new Combo(permission, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			dt1 = new Combo(permission, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		dt1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		dt1.setText("DataType");
		if (!isMac) {
			dt1.addListener(SWT.DROP_DOWN, event -> updateDataTypes(dt1));
		} else {
			dtAdd.addListener(SWT.Selection, event -> updateDataTypes(dt1));
		}

		Button permissionAdd = new Button(permission, SWT.PUSH);
		permissionAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		permissionAdd.setText("Add");
		permissionAdd.addListener(SWT.Selection, event -> archFunc.addPermission(comp5.getText(), comp6.getText(), dt1.getText()));

		// Revoke
		Group revoke = new Group(actions, SWT.SHADOW_IN);
		revoke.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		revoke.setLayout(new GridLayout(75, false));
		revoke.setText("Revoke");

		Combo comp13;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp13 = new Combo(revoke, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp13 = new Combo(revoke, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp13.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp13.setText("Component");
		if (!isMac) {
			comp13.addListener(SWT.DROP_DOWN, event -> updateComponents(comp13));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp13));
		}

		Label receiveFromLab2 = new Label(revoke, SWT.CENTER);
		receiveFromLab2.setText("gets Permission revoked from");

		Combo comp14;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp14 = new Combo(revoke, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp14 = new Combo(revoke, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp14.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp14.setText("Component");
		if (!isMac) {
			comp14.addListener(SWT.DROP_DOWN, event -> updateComponents(comp14));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp14));
		}

		Label withDtLab2 = new Label(revoke, SWT.CENTER);
		withDtLab2.setText("for the Data Type");

		Combo dt2;// = new Combo(delete, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			dt2 = new Combo(revoke, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			dt2 = new Combo(revoke, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		dt2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		dt2.setText("DataType");
		if (!isMac) {
			dt2.addListener(SWT.DROP_DOWN, event -> updateDataTypes(dt2));
		} else {
			dtAdd.addListener(SWT.Selection, event -> updateDataTypes(dt2));
		}

		Button revokeAdd = new Button(revoke, SWT.PUSH);
		revokeAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		revokeAdd.setText("Add");
		revokeAdd.addListener(SWT.Selection, event -> archFunc.addRevoke(comp13.getText(), comp14.getText(), dt2.getText()));

		// dependence relations
		Group deps = new Group(left, SWT.SHADOW_IN);
		deps.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deps.setLayout(new GridLayout(86, false));
		deps.setText("Dependence Relations");

		Combo comp9;// = new Combo(deps, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp9 = new Combo(deps, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp9 = new Combo(deps, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp9.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp9.setText("Component");
		if (!isMac) {
			comp9.addListener(SWT.DROP_DOWN, event -> updateComponents(comp9));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp9));
		}

		Label canLab = new Label(deps, SWT.CENTER);
		canLab.setText("has the computational power to arrive");

		Combo var3;// = new Combo(deps, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			var3 = new Combo(deps, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			var3 = new Combo(deps, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		var3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		var3.setText("Variable");
		if (!isMac) {
			var3.addListener(SWT.DROP_DOWN, event -> updateVariables(var3));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateVariables(var3));
		}

		Label fromLab3 = new Label(deps, SWT.CENTER);
		fromLab3.setText("from");

		Table varTable3 = new Table(deps, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		varTable3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 20, 10));
		varTable3.setToolTipText("Variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(varTable3));

		Label withLab = new Label(deps, SWT.CENTER);
		withLab.setText("with the probability of ");

		Text prob1 = new Text(deps, SWT.SINGLE);
		prob1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		prob1.setText("probability");
		prob1.setToolTipText("The probability of the dependece relation");
		prob1.addMouseListener(mouseListener);

		Button depAdd = new Button(deps, SWT.PUSH);
		depAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		depAdd.setText("Add");
		depAdd.addListener(SWT.Selection, event -> handleDep(comp9, var3, varTable3, prob1));

		// custom deductions
		Group mydeds = new Group(left, SWT.SHADOW_IN);
		mydeds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		mydeds.setLayout(new GridLayout(88, false));
		mydeds.setText("Custom Deductions");

		Text dedName = new Text(mydeds, SWT.SINGLE);
		dedName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		dedName.setText("name");
		dedName.setToolTipText("The name of the deduction");
		dedName.addMouseListener(mouseListener);

		Table premiseTable = new Table(mydeds, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		premiseTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 40, 10));
		premiseTable.setToolTipText("Premise Equations");
		eqAdd.addListener(SWT.Selection, event -> updateEquationsTab(premiseTable));

		Label dedLabel = new Label(mydeds, SWT.CENTER);
		dedLabel.setText("can be used to deduce this equation:");

		Combo conclusion;// = new Combo(mydeds, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			conclusion = new Combo(mydeds, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			conclusion = new Combo(mydeds, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		conclusion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		conclusion.setText("Conclusion Equation");
		if (!isMac) {
			conclusion.addListener(SWT.DROP_DOWN, event -> updateEquations(conclusion));
		} else {
			eqAdd.addListener(SWT.Selection, event -> updateEquations(conclusion));
		}

		Label withLab2 = new Label(mydeds, SWT.CENTER);
		withLab2.setText("with the probability of ");

		Text prob2 = new Text(mydeds, SWT.SINGLE);
		prob2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		prob2.setText("probability");
		prob2.setToolTipText("The probability of the custom deduction");
		prob2.addMouseListener(mouseListener);

		Button mydedAdd = new Button(mydeds, SWT.PUSH);
		mydedAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		mydedAdd.setText("Add");
		mydedAdd.addListener(SWT.Selection, event -> handlemyDed(dedName, premiseTable, conclusion, prob2));

		// deduction capabilities
		Group deds = new Group(left, SWT.SHADOW_IN);
		deds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deds.setLayout(new GridLayout(64, false));
		deds.setText("Deductions");

		Combo comp8;// = new Combo(deds, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			comp8 = new Combo(deds, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			comp8 = new Combo(deds, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		comp8.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		comp8.setText("Component");
		if (!isMac) {
			comp8.addListener(SWT.DROP_DOWN, event -> updateComponents(comp8));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(comp8));
		}

		Label can2Lab = new Label(deds, SWT.CENTER);
		can2Lab.setText("can deduce equations using");

		Table dedTable = new Table(deds, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		dedTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 20, 10));
		dedTable.setToolTipText("Standard Deductions");
		for (Deduction d : archFunc.getDeducs()) {
			TableItem item = new TableItem(dedTable, SWT.None);
			item.setText(d.toString());
		}
		mydedAdd.addListener(SWT.Selection, event -> updateDedTab(dedTable));

		Button dedAdd = new Button(deds, SWT.PUSH);
		dedAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		dedAdd.setText("Add");
		dedAdd.addListener(SWT.Selection, event -> handleDed(comp8, dedTable));

		// ##################### right side ##########################
		// components table
		Group components2 = new Group(right, SWT.SHADOW_IN);
		components2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		components2.setLayout(new GridLayout(1, true));
		components2.setText("Components");

		Table compTable = new Table(components2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		compTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		compTable.setToolTipText("List of all components");
		compAdd.addListener(SWT.Selection, event -> updateCompsTab(compTable));

		Button compRemove = new Button(components2, SWT.PUSH);
		compRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		compRemove.setText("Remove");
		compRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.COMP, compTable));

		// variables table
		Group variables2 = new Group(right, SWT.SHADOW_IN);
		variables2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		variables2.setLayout(new GridLayout(1, true));
		variables2.setText("Variables");

		Table variableTable = new Table(
				variables2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		variableTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		variableTable.setToolTipText("List of all variables");
		varAdd.addListener(SWT.Selection, event -> updateVarsTab(variableTable));

		Button varRemove = new Button(variables2, SWT.PUSH);
		varRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		varRemove.setText("Remove");
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, variableTable));
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, vars1));
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, vars2));
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, varTable));
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, varTable1));
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, varTable2));
		varRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.VAR, varTable3));

		// terms table
		Group terms2 = new Group(right, SWT.SHADOW_IN);
		terms2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		terms2.setLayout(new GridLayout(1, true));
		terms2.setText("Terms");

		Table termTable = new Table(terms2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		termTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		termTable.setToolTipText("List of all terms");
		termAdd.addListener(SWT.Selection, event -> updateTermsTab(termTable));
		varAdd.addListener(SWT.Selection, event -> updateTermsTab(termTable));

		Button termRemove = new Button(terms2, SWT.PUSH);
		termRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		termRemove.setText("Remove");
		termRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.TERM, termTable));

		// equations table
		Group equations2 = new Group(right, SWT.SHADOW_IN);
		equations2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		equations2.setLayout(new GridLayout(1, true));
		equations2.setText("Equations");

		Table equationTable = new Table(
				equations2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		equationTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		equationTable.setToolTipText("List of all equations");
		eqAdd.addListener(SWT.Selection, event -> updateEquationsTab(equationTable));

		Button eqRemove = new Button(equations2, SWT.PUSH);
		eqRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		eqRemove.setText("Remove");
		eqRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.EQ, equationTable));
		eqRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.EQ, eq3));
		eqRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.EQ, eqTable));
		eqRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.EQ, premiseTable));

		// data types table
		Group dataTypes2 = new Group(right, SWT.SHADOW_IN);
		dataTypes2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		dataTypes2.setLayout(new GridLayout(1, true));
		dataTypes2.setText("Data Types");

		Table dtTable = new Table(dataTypes2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		dtTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		dtTable.setToolTipText("List of all data types");
		dtAdd.addListener(SWT.Selection, event -> updateDataTypesTab(dtTable));

		Button dtRemove = new Button(dataTypes2, SWT.PUSH);
		dtRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		dtRemove.setText("Remove");
		dtRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.DT, dtTable));

		// trusts table
		Group trusts2 = new Group(right, SWT.SHADOW_IN);
		trusts2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		trusts2.setLayout(new GridLayout(1, true));
		trusts2.setText("Trust Relations");

		Table trustTable = new Table(trusts2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		trustTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		trustTable.setToolTipText("List of all trust relations");
		trustAdd.addListener(SWT.Selection, event -> updateTrustTab(trustTable));

		Button trustRemove = new Button(trusts2, SWT.PUSH);
		trustRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		trustRemove.setText("Remove");
		trustRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.TRUST, trustTable));

		// statements table
		Group statements2 = new Group(right, SWT.SHADOW_IN);
		statements2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		statements2.setLayout(new GridLayout(1, true));
		statements2.setText("Statements");

		Table stmtTable = new Table(statements2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		stmtTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		stmtTable.setToolTipText("List of all proofs and attestations");
		stAdd.addListener(SWT.Selection, event -> updateStatementTab(stmtTable));

		Button stRemove = new Button(statements2, SWT.PUSH);
		stRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		stRemove.setText("Remove");
		stRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.STMT, stmtTable));
		stRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.STMT, att1));
		stRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.STMT, stTable));

		// purposes table
		Group purposes2 = new Group(right, SWT.SHADOW_IN);
		purposes2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		purposes2.setLayout(new GridLayout(1, true));
		purposes2.setText("Purposes");

		Table purposeTable = new Table(purposes2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		purposeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		purposeTable.setToolTipText("List of all proofs and attestations");
		purpAdd.addListener(SWT.Selection, event -> updatePurpsTab(purposeTable));

		Button purpRemove = new Button(purposes2, SWT.PUSH);
		purpRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		purpRemove.setText("Remove");
		purpRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.PURP, purposeTable));
		purpRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.PURP, purps1));
		purpRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.PURP, purps2));

		// actions table
		Group actions2 = new Group(right, SWT.SHADOW_IN);
		actions2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		actions2.setLayout(new GridLayout(1, true));
		actions2.setText("Actions");

		Table actionTable = new Table(actions2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		actionTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		actionTable.setToolTipText("List of all actions");
		hasAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		computeAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		receiveAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		creceiveAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		preceiveAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		checkAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		deleteAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		verifAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		permissionAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		revokeAdd.addListener(SWT.Selection, event -> updateActionsTab(actionTable));

		Button actRemove = new Button(actions2, SWT.PUSH);
		actRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		actRemove.setText("Remove");
		actRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.ACT, actionTable));

		// dependence relations table
		Group deps2 = new Group(right, SWT.SHADOW_IN);
		deps2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deps2.setLayout(new GridLayout(1, true));
		deps2.setText("Dependence Relations");

		Table depTable = new Table(deps2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		depTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		depTable.setToolTipText("List of all dependece relations");
		depAdd.addListener(SWT.Selection, event -> updateDepsTab(depTable));

		Button depRemove = new Button(deps2, SWT.PUSH);
		depRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		depRemove.setText("Remove");
		depRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.DEP, depTable));

		// deduction capabilities table
		Group deds2 = new Group(right, SWT.SHADOW_IN);
		deds2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		deds2.setLayout(new GridLayout(1, true));
		deds2.setText("Deduction Capabilities");

		Table dedTable2 = new Table(deds2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		dedTable2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 10));
		dedTable2.setToolTipText("List of all dependece relations");
		dedAdd.addListener(SWT.Selection, event -> updateDedsTab(dedTable2));

		Button dedRemove = new Button(deds2, SWT.PUSH);
		dedRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		dedRemove.setText("Remove");
		dedRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.DED, dedTable2));

		// set the first tab
		tab1.setControl(bothSides1);

		// -------------------------- second tab -----------------------
		TabItem tab2 = new TabItem(folder, SWT.NONE);
		tab2.setText("Property Verification");

		// left and right sides
		Composite bothSides2 = new Composite(folder, SWT.NONE);
		bothSides2.setLayout(new GridLayout(2, true)); //false
		Composite left2 = new Composite(bothSides2, SWT.BORDER);
		left2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		left2.setLayout(new GridLayout(1, true));
		left2.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		Composite right2 = new Composite(bothSides2, SWT.BORDER);
		right2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		right2.setLayout(new GridLayout(1, true));

		finishButton.addListener(SWT.Selection, event -> recursiveSetEnabled(left2, true));
		finishButton.addListener(SWT.Selection, event -> recursiveSetEnabled(right2, true));
		finishButton.addListener(SWT.Selection, event -> recursiveSetEnabled(left, false));
		finishButton.addListener(SWT.Selection, event -> recursiveSetEnabled(right, false));
		editBtn.addListener(SWT.Selection, event -> recursiveSetEnabled(right, true));
		editBtn.addListener(SWT.Selection, event -> recursiveSetEnabled(left, true));
		editBtn.addListener(SWT.Selection, event -> recursiveSetEnabled(right2, false));
		editBtn.addListener(SWT.Selection, event -> recursiveSetEnabled(left2, false));

		// left side
		// first line
		Group properties = new Group(left2, SWT.SHADOW_IN);
		properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		properties.setLayout(new GridLayout(1, false));
		properties.setText("Properties");

		// line has
		Group hasProp = new Group(properties, SWT.SHADOW_IN);
		hasProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		hasProp.setLayout(new GridLayout(7, false));
		hasProp.setText("Has");

		Combo compProp;// = new Combo(hasProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			compProp = new Combo(hasProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			compProp = new Combo(hasProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		compProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		compProp.setText("Component");
		if (!isMac) {
			compProp.addListener(SWT.DROP_DOWN, event -> updateComponents(compProp));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(compProp));
		}

		Label hasLabProp = new Label(hasProp, SWT.CENTER);
		hasLabProp.setText("has with probability ");

		Text prob3 = new Text(hasProp, SWT.SINGLE);
		prob3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		prob3.setText("probability");
		prob3.setToolTipText("The minimal probability of the has property");
		prob3.addMouseListener(mouseListener);

		Combo varProp;// = new Combo(hasProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			varProp = new Combo(hasProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			varProp = new Combo(hasProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		varProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		varProp.setText("Variable");
		if (!isMac) {
			varProp.addListener(SWT.DROP_DOWN, event -> updateVariables(varProp));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateVariables(varProp));
		}

		Button hasPropAdd = new Button(hasProp, SWT.PUSH);
		hasPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		hasPropAdd.setText("Add");
		hasPropAdd.addListener(SWT.Selection,
				event -> archFunc.addPropHas(compProp.getText(), varProp.getText(), prob3.getText()));

		// line knows
		Group knowsProp = new Group(properties, SWT.SHADOW_IN);
		knowsProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		knowsProp.setLayout(new GridLayout(7, false));
		knowsProp.setText("Knows");

		Combo compProp1;// = new Combo(knowsProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			compProp1 = new Combo(knowsProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			compProp1 = new Combo(knowsProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		compProp1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		compProp1.setText("Component");
		if (!isMac) {
			compProp1.addListener(SWT.DROP_DOWN, event -> updateComponents(compProp1));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(compProp1));
		}

		Label knowsLabProp = new Label(knowsProp, SWT.CENTER);
		knowsLabProp.setText("knows with probability ");

		Text prob4 = new Text(knowsProp, SWT.SINGLE);
		prob4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		prob4.setText("probability");
		prob4.setToolTipText("The minimal probability of the knows property");
		prob4.addMouseListener(mouseListener);

		Combo eqProp;// = new Combo(knowsProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			eqProp = new Combo(knowsProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			eqProp = new Combo(knowsProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		eqProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		eqProp.setText("Equation");
		if (!isMac) {
			eqProp.addListener(SWT.DROP_DOWN, event -> updateEquations(eqProp));
		} else {
			eqAdd.addListener(SWT.Selection, event -> updateEquations(eqProp));
		}

		Button knowsPropAdd = new Button(knowsProp, SWT.PUSH);
		knowsPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		knowsPropAdd.setText("Add");
		knowsPropAdd.addListener(
				SWT.Selection, event -> archFunc.addPropKnows(compProp1.getText(), eqProp.getText(), prob4.getText()));

		// line notshared
		Group notsharedProp = new Group(properties, SWT.SHADOW_IN);
		notsharedProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		notsharedProp.setLayout(new GridLayout(5, false));
		notsharedProp.setText("NotShared");

		Combo compProp3;// = new Combo(notsharedProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			compProp3 = new Combo(notsharedProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			compProp3 = new Combo(notsharedProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		compProp3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		compProp3.setText("Component");
		if (!isMac) {
			compProp3.addListener(SWT.DROP_DOWN, event -> updateComponents(compProp3));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(compProp3));
		}

		Label notSharedLabProp = new Label(notsharedProp, SWT.CENTER);
		notSharedLabProp.setText("does not share");

		Combo varProp1;// = new Combo(notsharedProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			varProp1 = new Combo(notsharedProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			varProp1 = new Combo(notsharedProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		varProp1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		varProp1.setText("Variable");
		if (!isMac) {
			varProp1.addListener(SWT.DROP_DOWN, event -> updateVariables(varProp1));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateVariables(varProp1));
		}

		Label notSharedLabProp1 = new Label(notsharedProp, SWT.CENTER);
		notSharedLabProp1.setText("with a third party");

		Button notSharedPropAdd = new Button(notsharedProp, SWT.PUSH);
		notSharedPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		notSharedPropAdd.setText("Add");
		notSharedPropAdd.addListener(SWT.Selection,
				event -> archFunc.addPropNotShared(compProp3.getText(), varProp1.getText()));

		// line notstored
		Group notstoredProp = new Group(properties, SWT.SHADOW_IN);
		notstoredProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		notstoredProp.setLayout(new GridLayout(6, false));
		notstoredProp.setText("NotStored");

		Combo compProp4;// = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			compProp4 = new Combo(notstoredProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			compProp4 = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		compProp4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		compProp4.setText("Component");
		if (!isMac) {
			compProp4.addListener(SWT.DROP_DOWN, event -> updateComponents(compProp4));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(compProp4));
		}

		Label notStoredLabProp = new Label(notstoredProp, SWT.CENTER);
		notStoredLabProp.setText("does not store");

		Combo varProp2;// = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			varProp2 = new Combo(notstoredProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			varProp2 = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		varProp2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		varProp2.setText("Variable");
		if (!isMac) {
			varProp2.addListener(SWT.DROP_DOWN, event -> updateVariables(varProp2));
		} else {
			varAdd.addListener(SWT.Selection, event -> updateVariables(varProp2));
		}

		Label notStoredLabProp2 = new Label(notstoredProp, SWT.CENTER);
		notStoredLabProp2.setText("with bound");

		Combo boundProp;// = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			boundProp = new Combo(notstoredProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			boundProp = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		boundProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		boundProp.setText("Bound");
		for (int i=1; i<10; i++) {
			boundProp.add("" + i);
		}

		Button notStoredPropAdd = new Button(notstoredProp, SWT.PUSH);
		notStoredPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		notStoredPropAdd.setText("Add");
		notStoredPropAdd.addListener(SWT.Selection,
				event -> archFunc.addPropNotStored(compProp4.getText(), varProp2.getText(), boundProp.getText()));

		// not Purp property
		Group purpProp = new Group(properties, SWT.SHADOW_IN);
		purpProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		purpProp.setLayout(new GridLayout(84, false));
		purpProp.setText("Purpose Limitation");

		Combo compProp10;// = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			compProp10 = new Combo(purpProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			compProp10 = new Combo(purpProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		compProp10.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		compProp10.setText("Component");
		if (!isMac) {
			compProp10.addListener(SWT.DROP_DOWN, event -> updateComponents(compProp10));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(compProp10));
		}

		Label notPurpLabProp = new Label(purpProp, SWT.CENTER);
		notPurpLabProp.setText("does not fulfil purpose limitation");

		Button purpPropAdd = new Button(purpProp, SWT.PUSH);
		purpPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		purpPropAdd.setText("Add");
		purpPropAdd.addListener(
				SWT.Selection, event -> archFunc.addPropPurp(compProp10.getText()));
		
		// consent violated property
		Group consentProp = new Group(properties, SWT.SHADOW_IN);
		consentProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		consentProp.setLayout(new GridLayout(84, false));
		consentProp.setText("Consent Violation");

		Combo compProp11;// = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			compProp11 = new Combo(consentProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			compProp11 = new Combo(consentProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		compProp11.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		compProp11.setText("Component");
		if (!isMac) {
			compProp11.addListener(SWT.DROP_DOWN, event -> updateComponents(compProp11));
		} else {
			compAdd.addListener(SWT.Selection, event -> updateComponents(compProp11));
		}

		Label violatesLabProp = new Label(consentProp, SWT.CENTER);
		violatesLabProp.setText("violates its consent regarding");
		
		Combo dtProp;// = new Combo(notstoredProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			dtProp = new Combo(consentProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			dtProp = new Combo(consentProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		dtProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 2));
		dtProp.setText("Data Type");
		if (!isMac) {
			dtProp.addListener(SWT.DROP_DOWN, event -> updateDataTypes(dtProp));
		} else {
			dtAdd.addListener(SWT.Selection, event -> updateDataTypes(dtProp));
		}

		Button consentPropAdd = new Button(consentProp, SWT.PUSH);
		consentPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		consentPropAdd.setText("Add");
		consentPropAdd.addListener(SWT.Selection, event -> archFunc.addPropConsent(compProp11.getText(), dtProp.getText()));

		// line negation
		Group negProp = new Group(properties, SWT.SHADOW_IN);
		negProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		negProp.setLayout(new GridLayout(84, false));
		negProp.setText("Negation");

		Label notLabProp = new Label(negProp, SWT.CENTER);
		notLabProp.setText("NOT");

		Combo propProp;// = new Combo(negProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			propProp = new Combo(negProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			propProp = new Combo(negProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		propProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
		propProp.setText("Property");
		if (!isMac) {
			propProp.addListener(SWT.DROP_DOWN, event -> updateProps(propProp));
		}

		Button negPropAdd = new Button(negProp, SWT.PUSH);
		negPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		negPropAdd.setText("Add");
		negPropAdd.addListener(
				SWT.Selection, event -> archFunc.addPropNeg(propProp.getText()));

		// line composition
		Group conjProp = new Group(properties, SWT.SHADOW_IN);
		conjProp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		conjProp.setLayout(new GridLayout(84, false));
		conjProp.setText("Composition");

		Combo propProp1;// = new Combo(conjProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			propProp1 = new Combo(conjProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			propProp1 = new Combo(conjProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		propProp1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
		propProp1.setText("Property1");
		if (!isMac) {
			propProp1.addListener(SWT.DROP_DOWN, event -> updateProps(propProp1));
		}

		Label andLabProp = new Label(conjProp, SWT.CENTER);
		andLabProp.setText("AND");

		Combo propProp2;// = new Combo(conjProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		if (isMac) {
			propProp2 = new Combo(conjProp, SWT.SIMPLE | SWT.READ_ONLY);
		} else {
			propProp2 = new Combo(conjProp, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		propProp2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
		propProp2.setText("Property2");
		if (!isMac) {
			propProp2.addListener(SWT.DROP_DOWN, event -> updateProps(propProp2));
		}

		Button conjPropAdd = new Button(conjProp, SWT.PUSH);
		conjPropAdd.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		conjPropAdd.setText("Add");
		conjPropAdd.addListener(
				SWT.Selection, event -> archFunc.addPropConj(propProp1.getText(), propProp2.getText()));

		// Events for property combos
		if (isMac) {
			hasPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			knowsPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			notSharedPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			notStoredPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			negPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			purpPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			conjPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			consentPropAdd.addListener(SWT.Selection, event -> updateProps(propProp));
			hasPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			knowsPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			notSharedPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			notStoredPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			negPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			purpPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			conjPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			consentPropAdd.addListener(SWT.Selection, event -> updateProps(propProp1));
			hasPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			knowsPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			notSharedPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			notStoredPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			negPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			purpPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			conjPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
			consentPropAdd.addListener(SWT.Selection, event -> updateProps(propProp2));
		}

		// second line
		Group verification = new Group(left2, SWT.SHADOW_IN);
		verification.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		verification.setLayout(new GridLayout(122, false));
		verification.setText("Verification");

		Combo prop;// = new Combo(verification, SWT.DROP_DOWN | SWT.H_SCROLL | SWT.READ_ONLY);
		if (isMac) {
			prop = new Combo(verification, SWT.SIMPLE | SWT.H_SCROLL | SWT.READ_ONLY);
		} else {
			prop = new Combo(verification, SWT.DROP_DOWN | SWT.H_SCROLL | SWT.READ_ONLY);
		}
		prop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
		prop.setText("Property");
		if (!isMac) {
			prop.addListener(SWT.DROP_DOWN, event -> updateProps(prop));
		} else {
			hasPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
			knowsPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
			notSharedPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
			notStoredPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
			negPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
			purpPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
			conjPropAdd.addListener(SWT.Selection, event -> updateProps(prop));
		}

		Button verifyBtn = new Button(verification, SWT.PUSH);
		verifyBtn.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		verifyBtn.setText("Verify");

		// third line
		Group verified = new Group(left2, SWT.SHADOW_IN);
		verified.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		verified.setLayout(new GridLayout(122, false));
		verified.setText("Verified Properties");

		Table verifiedProps = new Table(verified, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		verifiedProps.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 15));
		verifiedProps.setToolTipText("List of all verified properties");
		verifiedProps.addListener(SWT.Selection, event -> updateVerifTab(event, verifiedProps));
		verifyBtn.addListener(SWT.Selection, event -> verifyProp(prop.getText(), verifiedProps));

		Button inspect = new Button(verified, SWT.PUSH);
		inspect.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		inspect.setText("Inspect Proof");
		inspect.addListener(SWT.Selection, event -> showTrace(verifiedProps));

		// ################## right side ##################################
		// first line
		Group props = new Group(right2, SWT.SHADOW_IN);
		props.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		props.setLayout(new GridLayout(1, true));
		props.setText("Properties");

		Table propTable = new Table(props, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		propTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		propTable.setToolTipText("List of all architecture properties to verify");
		hasPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		knowsPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		notSharedPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		notStoredPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		conjPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		negPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		purpPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		consentPropAdd.addListener(SWT.Selection, event -> updatePropsTab(propTable));

		Button propRemove = new Button(props, SWT.PUSH);
		propRemove.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		propRemove.setText("Remove");
		propRemove.addListener(SWT.Selection, event -> handleRemove(ObjectType.PROP, propTable));

		// set second tab
		tab2.setControl(bothSides2);

		// initially disable the second tab
		recursiveSetEnabled(left2, false);
		recursiveSetEnabled(right2, false);

		// load/save/reset events
		resetBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (showMessage(MessageType.WARN, "Should the architecture really be reset?")) {
					reset();
					//recursiveUpdate(folder);
					verifiedProps.removeAll();
					finishButton.setEnabled(true);
					loadButton.setEnabled(true);
					saveButton.setEnabled(true);
					loadButton2.setEnabled(true);
					folder.setSelection(0);
					drawButton.setEnabled(false);
					startIA.setEnabled(false);
					recursiveSetEnabled(right, true);
					recursiveSetEnabled(right2, false);
					recursiveSetEnabled(left, true);
					recursiveSetEnabled(left2, false);
				}
			}
		});		
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (archFunc.save2file(archName.getText())) {
					showMessage(MessageType.INF,
							"The current architecture was successfully saved to disk.");
				}
			}
		});
		loadButton2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (showMessage(MessageType.WARN,
						"The current architecture will be overwritten by the load. Continue?")) {
					ArchitectureFunctions tmp_arch = SaveLoadArch.loadArch(archName.getText());
					if (tmp_arch != null) {
						archFunc = tmp_arch;
						verifiedProps.removeAll();
					} else {
						showMessage(MessageType.INF,
								"No Architecture with the name " + archName.getText() + " found");
					}
				}
			}
		});
		loadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (showMessage(MessageType.WARN,
						"The current architecture will be overwritten by the load. Continue?")) {
					archFunc.load(examples.getText());
					archFunc.finish();
					verifiedProps.removeAll();
					//loadButton.setEnabled(false);
					finishButton.setEnabled(false);
					folder.setSelection(1);
					drawButton.setEnabled(true);
					startIA.setEnabled(true);
					recursiveSetEnabled(right, false);
					recursiveSetEnabled(right2, true);
					recursiveSetEnabled(left, false);
					recursiveSetEnabled(left2, true);
				}
			}
		});
		// events triggered by reset and loads
		//resetBtn.addListener(SWT.Selection, event -> recursiveUpdate(right));
		resetBtn.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		//resetBtn.addListener(SWT.Selection, event -> updateVerifiedTab(verifiedProps));
		resetBtn.addListener(SWT.Selection, event -> updateDedsTab(dedTable2));
		resetBtn.addListener(SWT.Selection, event -> updateDepsTab(depTable));
		resetBtn.addListener(SWT.Selection, event -> updateDedsTab(dedTable));
		resetBtn.addListener(SWT.Selection, event -> updateEquationsTab(premiseTable));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(varTable3));
		resetBtn.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		resetBtn.addListener(SWT.Selection, event -> updateStatementsTab(stmtTable));
		resetBtn.addListener(SWT.Selection, event -> updateTrustTab(trustTable));
		resetBtn.addListener(SWT.Selection, event -> updateEquationsTab(equationTable));
		resetBtn.addListener(SWT.Selection, event -> updateTermsTab(termTable));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(variableTable));
		resetBtn.addListener(SWT.Selection, event -> updateCompsTab(compTable));
		//resetBtn.addListener(SWT.Selection, event -> updateEquationsTab(eqTable2));
		resetBtn.addListener(SWT.Selection, event -> updateEquationsTab(eqTable));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(varTable));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(varTable1));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(varTable2));
		resetBtn.addListener(SWT.Selection, event -> updateStatementsTab(stTable));
		resetBtn.addListener(SWT.Selection, event -> updateAttestsTab(att1));
		resetBtn.addListener(SWT.Selection, event -> updateEquationsTab(eq3));
		resetBtn.addListener(SWT.Selection, event -> updatePurpsTab(purposeTable));
		resetBtn.addListener(SWT.Selection, event -> updatePurpsTab(purps1));
		resetBtn.addListener(SWT.Selection, event -> updatePurpsTab(purps2));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(vars1));
		resetBtn.addListener(SWT.Selection, event -> updateVarsTab(vars2));
		resetBtn.addListener(SWT.Selection, event -> updateDataTypesTab(dtTable));
		//loadButton.addListener(SWT.Selection, event -> updateVerifiedTab(verifiedProps));
		//loadButton2.addListener(SWT.Selection, event -> updateVerifiedTab(verifiedProps));
		loadButton.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		loadButton2.addListener(SWT.Selection, event -> updatePropsTab(propTable));
		loadButton.addListener(SWT.Selection, event -> updateDedsTab(dedTable2));
		loadButton2.addListener(SWT.Selection, event -> updateDedsTab(dedTable2));
		loadButton.addListener(SWT.Selection, event -> updateDepsTab(depTable));
		loadButton2.addListener(SWT.Selection, event -> updateDepsTab(depTable));
		loadButton.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		loadButton2.addListener(SWT.Selection, event -> updateActionsTab(actionTable));
		loadButton.addListener(SWT.Selection, event -> updateStatementTab(stmtTable));
		loadButton2.addListener(SWT.Selection, event -> updateStatementTab(stmtTable));
		loadButton.addListener(SWT.Selection, event -> updateTrustTab(trustTable));
		loadButton2.addListener(SWT.Selection, event -> updateTrustTab(trustTable));
		loadButton.addListener(SWT.Selection, event -> updateEquationsTab(equationTable));
		loadButton2.addListener(SWT.Selection, event -> updateEquationsTab(equationTable));
		loadButton.addListener(SWT.Selection, event -> updateTermsTab(termTable));
		loadButton2.addListener(SWT.Selection, event -> updateTermsTab(termTable));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(variableTable));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(variableTable));
		loadButton.addListener(SWT.Selection, event -> updateCompsTab(compTable));
		loadButton2.addListener(SWT.Selection, event -> updateCompsTab(compTable));
		loadButton.addListener(SWT.Selection, event -> updateDedTab(dedTable));
		loadButton2.addListener(SWT.Selection, event -> updateDedTab(dedTable));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(varTable3));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(varTable3));
		loadButton.addListener(SWT.Selection, event -> updateEquationsTab(premiseTable));
		loadButton2.addListener(SWT.Selection, event -> updateEquationsTab(premiseTable));
		//loadButton.addListener(SWT.Selection, event -> updateEquationsTab(eqTable2));
		//loadButton2.addListener(SWT.Selection, event -> updateEquationsTab(eqTable2));
		loadButton.addListener(SWT.Selection, event -> updateEquationsTab(eqTable));
		loadButton2.addListener(SWT.Selection, event -> updateEquationsTab(eqTable));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(varTable));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(varTable));
		loadButton.addListener(SWT.Selection, event -> updateStatementsTab(stTable));
		loadButton2.addListener(SWT.Selection, event -> updateStatementsTab(stTable));
		loadButton.addListener(SWT.Selection, event -> updateAttestsTab(att1));
		loadButton2.addListener(SWT.Selection, event -> updateAttestsTab(att1));
		loadButton.addListener(SWT.Selection, event -> updateEquationsTab(eq3));
		loadButton2.addListener(SWT.Selection, event -> updateEquationsTab(eq3));
		loadButton.addListener(SWT.Selection, event -> updatePurpsTab(purps1));
		loadButton2.addListener(SWT.Selection, event -> updatePurpsTab(purps1));
		loadButton.addListener(SWT.Selection, event -> updatePurpsTab(purps2));
		loadButton2.addListener(SWT.Selection, event -> updatePurpsTab(purps2));
		loadButton.addListener(SWT.Selection, event -> updatePurpsTab(purposeTable));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(vars1));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(vars2));
		loadButton2.addListener(SWT.Selection, event -> updatePurpsTab(purposeTable));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(vars1));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(vars2));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(varTable1));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(varTable1));
		loadButton.addListener(SWT.Selection, event -> updateVarsTab(varTable2));
		loadButton2.addListener(SWT.Selection, event -> updateVarsTab(varTable2));
		loadButton.addListener(SWT.Selection, event -> updateDataTypesTab(dtTable));
		loadButton2.addListener(SWT.Selection, event -> updateDataTypesTab(dtTable));

		if (isMac) {
			resetBtn.addListener(SWT.Selection, event -> updateTerms(term1));
			loadButton.addListener(SWT.Selection, event -> updateTerms(term1));
			loadButton2.addListener(SWT.Selection, event -> updateTerms(term1));
			resetBtn.addListener(SWT.Selection, event -> updateTerms(term2));
			loadButton.addListener(SWT.Selection, event -> updateTerms(term2));
			loadButton2.addListener(SWT.Selection, event -> updateTerms(term2));
			resetBtn.addListener(SWT.Selection, event -> updateTerms(term3));
			loadButton.addListener(SWT.Selection, event -> updateTerms(term3));
			loadButton2.addListener(SWT.Selection, event -> updateTerms(term3));
			resetBtn.addListener(SWT.Selection, event -> updateTerms(t1));
			loadButton.addListener(SWT.Selection, event -> updateTerms(t1));
			loadButton2.addListener(SWT.Selection, event -> updateTerms(t1));
			resetBtn.addListener(SWT.Selection, event -> updateTerms(t2));
			loadButton.addListener(SWT.Selection, event -> updateTerms(t2));
			loadButton2.addListener(SWT.Selection, event -> updateTerms(t2));
			resetBtn.addListener(SWT.Selection, event -> updateEquations(eq1));
			loadButton.addListener(SWT.Selection, event -> updateEquations(eq1));
			loadButton2.addListener(SWT.Selection, event -> updateEquations(eq1));
			resetBtn.addListener(SWT.Selection, event -> updateEquations(eq2));
			loadButton.addListener(SWT.Selection, event -> updateEquations(eq2));
			loadButton2.addListener(SWT.Selection, event -> updateEquations(eq2));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(c1));
			loadButton.addListener(SWT.Selection, event -> updateComponents(c1));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(c1));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(c2));
			loadButton.addListener(SWT.Selection, event -> updateComponents(c2));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(c2));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(c3));
			loadButton.addListener(SWT.Selection, event -> updateComponents(c3));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(c3));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp1));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp1));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp1));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp2));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp2));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp2));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp3));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp3));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp3));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp4));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp4));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp4));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp5));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp5));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp5));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp6));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp6));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp6));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp7));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp7));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp7));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp8));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp8));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp8));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp9));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp9));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp9));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp10));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp10));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp10));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp11));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp11));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp11));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp12));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp12));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp12));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp13));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp13));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp13));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp14));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp14));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp14));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp15));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp15));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp15));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(comp16));
			loadButton.addListener(SWT.Selection, event -> updateComponents(comp16));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(comp16));
			resetBtn.addListener(SWT.Selection, event -> updateVariables(var));
			loadButton.addListener(SWT.Selection, event -> updateVariables(var));
			loadButton2.addListener(SWT.Selection, event -> updateVariables(var));
			resetBtn.addListener(SWT.Selection, event -> updateVariables(var1));
			loadButton.addListener(SWT.Selection, event -> updateVariables(var1));
			loadButton2.addListener(SWT.Selection, event -> updateVariables(var1));
			resetBtn.addListener(SWT.Selection, event -> updateVariables(var3));
			loadButton.addListener(SWT.Selection, event -> updateVariables(var3));
			loadButton2.addListener(SWT.Selection, event -> updateVariables(var3));
			resetBtn.addListener(SWT.Selection, event -> updateEquations(eq));
			loadButton.addListener(SWT.Selection, event -> updateEquations(eq));
			loadButton2.addListener(SWT.Selection, event -> updateEquations(eq));
			resetBtn.addListener(SWT.Selection, event -> updateProofs(proofs));
			loadButton.addListener(SWT.Selection, event -> updateProofs(proofs));
			loadButton2.addListener(SWT.Selection, event -> updateProofs(proofs));
			resetBtn.addListener(SWT.Selection, event -> updateAttests(attests));
			loadButton.addListener(SWT.Selection, event -> updateAttests(attests));
			loadButton2.addListener(SWT.Selection, event -> updateAttests(attests));
			resetBtn.addListener(SWT.Selection, event -> updateEquations(conclusion));
			loadButton.addListener(SWT.Selection, event -> updateEquations(conclusion));
			loadButton2.addListener(SWT.Selection, event -> updateEquations(conclusion));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(compProp));
			loadButton.addListener(SWT.Selection, event -> updateComponents(compProp));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(compProp));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(compProp1));
			loadButton.addListener(SWT.Selection, event -> updateComponents(compProp1));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(compProp1));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(compProp3));
			loadButton.addListener(SWT.Selection, event -> updateComponents(compProp3));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(compProp3));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(compProp4));
			loadButton.addListener(SWT.Selection, event -> updateComponents(compProp4));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(compProp4));
			resetBtn.addListener(SWT.Selection, event -> updateComponents(compProp10));
			loadButton.addListener(SWT.Selection, event -> updateComponents(compProp10));
			loadButton2.addListener(SWT.Selection, event -> updateComponents(compProp10));
			resetBtn.addListener(SWT.Selection, event -> updateVariables(varProp));
			loadButton.addListener(SWT.Selection, event -> updateVariables(varProp));
			loadButton2.addListener(SWT.Selection, event -> updateVariables(varProp));
			resetBtn.addListener(SWT.Selection, event -> updateVariables(varProp1));
			loadButton.addListener(SWT.Selection, event -> updateVariables(varProp1));
			loadButton2.addListener(SWT.Selection, event -> updateVariables(varProp1));
			resetBtn.addListener(SWT.Selection, event -> updateVariables(varProp2));
			loadButton.addListener(SWT.Selection, event -> updateVariables(varProp2));
			loadButton2.addListener(SWT.Selection, event -> updateVariables(varProp2));
			resetBtn.addListener(SWT.Selection, event -> updateEquations(eqProp));
			loadButton.addListener(SWT.Selection, event -> updateEquations(eqProp));
			loadButton2.addListener(SWT.Selection, event -> updateEquations(eqProp));
			resetBtn.addListener(SWT.Selection, event -> updateProps(propProp));
			loadButton.addListener(SWT.Selection, event -> updateProps(propProp));
			loadButton2.addListener(SWT.Selection, event -> updateProps(propProp));
			resetBtn.addListener(SWT.Selection, event -> updateProps(propProp1));
			loadButton.addListener(SWT.Selection, event -> updateProps(propProp1));
			loadButton2.addListener(SWT.Selection, event -> updateProps(propProp1));
			resetBtn.addListener(SWT.Selection, event -> updateProps(propProp2));
			loadButton.addListener(SWT.Selection, event -> updateProps(propProp2));
			loadButton2.addListener(SWT.Selection, event -> updateProps(propProp2));
			resetBtn.addListener(SWT.Selection, event -> updateProps(prop));
			loadButton.addListener(SWT.Selection, event -> updateProps(prop));
			loadButton2.addListener(SWT.Selection, event -> updateProps(prop));
			resetBtn.addListener(SWT.Selection, event -> updateProps(purp1));
			loadButton.addListener(SWT.Selection, event -> updateProps(purp1));
			loadButton2.addListener(SWT.Selection, event -> updateProps(purp1));
			resetBtn.addListener(SWT.Selection, event -> updateProps(dt));
			loadButton.addListener(SWT.Selection, event -> updateProps(dt));
			loadButton2.addListener(SWT.Selection, event -> updateProps(dt));
			resetBtn.addListener(SWT.Selection, event -> updateProps(dt1));
			loadButton.addListener(SWT.Selection, event -> updateProps(dt1));
			loadButton2.addListener(SWT.Selection, event -> updateProps(dt1));
			resetBtn.addListener(SWT.Selection, event -> updateProps(dt2));
			loadButton.addListener(SWT.Selection, event -> updateProps(dt2));
			loadButton2.addListener(SWT.Selection, event -> updateProps(dt2));
		}


		// finishing touch
		sc.setContent(everything);
		sc.setMinSize(everything.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);

		shell.setMaximized(true);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	/**
	 * Helper method to dis/enable controls recursively in a composite chain.
	 * 
	 * @param ctrl
	 *          the start element
	 * @param enabled
	 *          true or false
	 */
	private void recursiveSetEnabled(Control ctrl, boolean enabled) {
		if (ctrl instanceof Composite) {
			// for composites make recursive call
			Composite comp = (Composite) ctrl;
			for (Control c : comp.getChildren()) {
				recursiveSetEnabled(c, enabled);
			}
		} else {
			// dis/enable all 'leaf' controls
			ctrl.setEnabled(enabled);
		}
	}

	/**
	 * Method to reset the architecture and the properties.
	 */
	private static void reset() {
		// reset the architecture objects and all properties
		archFunc = new ArchitectureFunctions();
	}

	/**
	 * Method that opens a new shell and displays the current purpose hierarchy.
	 */
	private static void showHierarchy() {
		PurposeHierarchy purpHier = archFunc.getPurpHier();
		final Shell shell = new Shell(display);
		shell.setMaximized(true);
		shell.setText("Purpose Hierarchy");
		final LightweightSystem lws = new LightweightSystem(shell);
		Figure contents = new Figure();
		int width = purpHier.getPurposes().size();
		org.eclipse.draw2d.GridLayout gridLayout = new org.eclipse.draw2d.GridLayout(width, false);
		gridLayout.horizontalSpacing = 60;
		gridLayout.verticalSpacing = 60;
		contents.setLayoutManager(gridLayout);
		Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
		Font regularFont = new Font(null, "Arial", 10, SWT.NONE);

		//List<ComponentFigure> purposes = new ArrayList<ComponentFigure>();
		ComponentFigure [] purposesArray = new ComponentFigure[width];
		int mid = (int) Math.floor(width/2);
		// add dummy purposes for alignment
		for (int i=0; i < mid; i++) {
			// add dummy
			contents.add(new ComponentFigure(new org.eclipse.draw2d.Label("")));
		}
		// create a class object for the top element
		org.eclipse.draw2d.Label purposeNameT = new org.eclipse.draw2d.Label(purpHier.getTop().toString());
		purposeNameT.setFont(classFont);
		final ComponentFigure purposeFigureT = new ComponentFigure(purposeNameT);
		contents.add(purposeFigureT);
		//purposes.add(purposeFigureT);
		purposesArray[0] = purposeFigureT;
		// create a class object for the bot element
		org.eclipse.draw2d.Label purposeNameB = new org.eclipse.draw2d.Label(purpHier.getBot().toString());
		purposeNameB.setFont(classFont);
		final ComponentFigure purposeFigureB = new ComponentFigure(purposeNameB);
		//purposes.add(purposeFigureB);
		purposesArray[1] = purposeFigureB;
		// add dummy purposes to fill remaining space
		for (int i=0; i < width-mid-1; i++) {
			// add dummy
			contents.add(new ComponentFigure(new org.eclipse.draw2d.Label("")));
		}
		for (int i=0; i<purpHier.getPurposes().size(); i++) {
			int numChildren = boolSum(purpHier.getAM()[i]);
			if (numChildren > 0) {
				for (int j=0; j<purpHier.getPurposes().size(); j++) {
					if (purpHier.getAM()[i][j] && j > 1) {
						// create a class object for the remaining purposes
						org.eclipse.draw2d.Label purposeName = new org.eclipse.draw2d.Label(purpHier.getPurposes().get(j).toString());
						purposeName.setFont(classFont);
						final ComponentFigure purposeFigure = new ComponentFigure(purposeName);
						contents.add(purposeFigure);
						//purposes.add(purposeFigure);
						purposesArray[j] = purposeFigure;
					} else {
						// add dummy
						contents.add(new ComponentFigure(new org.eclipse.draw2d.Label("")));
					}
				}
			}
		}
		// add dummy purposes for alignment
		for (int i=0; i < mid; i++) {
			// add dummy
			contents.add(new ComponentFigure(new org.eclipse.draw2d.Label("")));
		}
		// add the bot element
		contents.add(purposeFigureB);
		// add dummy purposes to fill remaining space
		for (int i=0; i < width-mid-1; i++) {
			//TODO Test: add dummy
			contents.add(new ComponentFigure(new org.eclipse.draw2d.Label("")));
		}

		// go through the purpose hierarchy and add connections
		List<PolylineConnection> connections = new ArrayList<PolylineConnection>();
		for (int i=0; i<purposesArray.length; i++) {
			for (int j=0; j<purposesArray.length; j++) {
				if (purpHier.getAM()[i][j]) {
					PolylineConnection connection = new PolylineConnection();
					connection.setLineWidth(2);
					connection.setFont(regularFont);
					connection.setConnectionRouter(new FanRouter());
					connection.setSourceAnchor(
							new ChopboxAnchor(purposesArray[i]));
					connection.setTargetAnchor(
							new ChopboxAnchor(purposesArray[j]));
					// put the finished connection onto the plane
					contents.add(connection);
					connections.add(connection);
				}
			}
		}

		lws.setContents(contents);
		shell.open();
		// main loop
		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Helper method that open a new shell for the data protection impact assessment.
	 */
	private static void impactAssessment() {
		// TODO finish
		// TODO color-coding for level
		// 1. table with data types that have to get impact values 1-4
		final Shell shell = new Shell(display);
		shell.setMaximized(true);
		shell.setText("Impact Assessment");
		shell.setLayout(new FillLayout());

		Composite everything = new Composite(shell, SWT.BORDER);
		everything.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		everything.setLayout(new GridLayout(1, false));
		everything.setBackground(display.getSystemColor(SWT.COLOR_GRAY));

		// first step
		Group dataTypes = new Group(everything, SWT.SHADOW_IN);
		dataTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		dataTypes.setLayout(new GridLayout(12, false));
		dataTypes.setText("Data Types");

		Table dt = new Table(dataTypes, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		dt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		dt.setToolTipText("Data Types in the Architecture");
		TableColumn column1 = new TableColumn(dt, SWT.NONE);
		TableColumn column2 = new TableColumn(dt, SWT.NONE);
		for (DataType d : archFunc.getdtSet()) {
			TableItem item = new TableItem(dt, SWT.NONE);
			item.setText(new String[] {d.toString(), "replace with level"});
		}
		column1.pack();
		column2.pack();

		// stuff for editable column
		final TableEditor editor = new TableEditor(dt);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		dt.addListener(SWT.Selection, e -> {
			// Clean up any previous editor control
			Control oldEditor = editor.getEditor();
			if (oldEditor != null)
				oldEditor.dispose();

			// Identify the selected row
			TableItem item = (TableItem) e.item;
			if (item == null)
				return;

			// The control that will be the editor must be a child of the Table
			Text newEditor = new Text(dt, SWT.NONE);
			newEditor.setText(item.getText(EDITABLECOLUMN));
			newEditor.addModifyListener(me -> {
				Text text = (Text) editor.getEditor();
				editor.getItem().setText(EDITABLECOLUMN, text.getText());
			});
			newEditor.selectAll();
			newEditor.setFocus();
			editor.setEditor(newEditor, item, EDITABLECOLUMN);
		});

		// 2. list possible properties to check for (user extendible)
		// second step
		Group properties = new Group(everything, SWT.SHADOW_IN);
		properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		properties.setLayout(new GridLayout(122, false));
		properties.setText("Properties");

		Table propertyTable = new Table(properties, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		propertyTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 15));
		propertyTable.setToolTipText("Properties to verify");
		for (Property p : archFunc.getpSet()) {
			if (p.getType() == PropertyType.CONSENTVIOLATED) {
				TableItem item = new TableItem(propertyTable, SWT.NONE);
				item.setText(p.toString());
			}
		}

		// 3. verify properties
		// third step
		Button verify = new Button(properties, SWT.PUSH);
		verify.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		verify.setText("Verify");
		
		Group violation = new Group(everything, SWT.SHADOW_IN);
		violation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		violation.setLayout(new GridLayout(12, false));
		violation.setText("Violations");
		
		Table violations = new Table(violation, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		violations.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 5));
		violations.setToolTipText("Occurrences of Privacy Breaches");
		TableColumn column1_2 = new TableColumn(violations, SWT.NONE);
		TableColumn column2_2 = new TableColumn(violations, SWT.NONE);
		
		// probably redundant stuff for editable column
		final TableEditor editor2 = new TableEditor(violations);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor2.horizontalAlignment = SWT.LEFT;
		editor2.grabHorizontal = true;
		editor2.minimumWidth = 50;

		violations.addListener(SWT.Selection, e -> {
			// Clean up any previous editor control
			Control oldEditor = editor2.getEditor();
			if (oldEditor != null)
				oldEditor.dispose();

			// Identify the selected row
			TableItem item = (TableItem) e.item;
			if (item == null)
				return;

			// The control that will be the editor must be a child of the Table
			Text newEditor = new Text(violations, SWT.NONE);
			newEditor.setText(item.getText(EDITABLECOLUMN));
			newEditor.addModifyListener(me -> {
				Text text = (Text) editor2.getEditor();
				editor2.getItem().setText(EDITABLECOLUMN, text.getText());
			});
			newEditor.selectAll();
			newEditor.setFocus();
			editor2.setEditor(newEditor, item, EDITABLECOLUMN);
		});

		// 4. for all violation: let user set probability level 1-4
		// 5. calculate overall risk level

		// fifth step
		Group risk = new Group(everything, SWT.SHADOW_IN);
		risk.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		risk.setLayout(new GridLayout(12, false));
		risk.setText("Risk");
		
		Button calculate = new Button(risk, SWT.PUSH);
		calculate.setLayoutData(new GridData(SWT.MIN, SWT.FILL, false, false, 1, 1));
		calculate.setText("Calculate");
		calculate.setEnabled(false);
		verify.addListener(SWT.Selection, event -> {
			for (TableItem i : propertyTable.getItems()) {
				if (i.getChecked()) {
					if (archFunc.verify(i.getText())) {
						// there is a violation
						// hence add property to violation table
						TableItem item = new TableItem(violations, SWT.NONE);
						item.setText(new String[] {i.getText(), "replace with level"});
					}
				}
			}
			column1_2.pack();
			column2_2.pack();
			calculate.setEnabled(true);
		});
		
		Label levelText = new Label(risk, SWT.CENTER);
		levelText.setText("The risk level is: ");
		Label level = new Label(risk, SWT.CENTER);
		level.setText("00");

		calculate.addListener(SWT.Selection, event -> {
			int maxlvl = 1;
			for (TableItem i : violations.getItems()) {
				// multiply impact level of data type with probability level of violation
				int lvl = 0;
				try {
					lvl = Integer.parseInt(i.getText(1)) * impactLevel(i.getText(), dt);
				} catch (NumberFormatException e) {
					// TODO
					e.printStackTrace();
				}
				if (lvl > maxlvl) {
					maxlvl = lvl;
				}
			}
			level.setText(Integer.toString(maxlvl));
			// coloring
			if (maxlvl == 0) {
				level.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			} else if (maxlvl < 3) {
				level.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
			} else if (maxlvl < 10) {
				level.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
			} else {
				level.setBackground(display.getSystemColor(SWT.COLOR_RED));
			}
		});

		shell.open();
		// main loop
		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Helper method to extract the impact level from a property.
	 * The method takes the data type from a ConsentViolated property
	 * and looks up the impact level in the table.
	 * @param property
	 * 			the property as a string
	 * @param dt
	 * 			the table of data types
	 * @return
	 * 			the impact level
	 */
	private static int impactLevel(String property, Table dt) {
		// TODO test
		int lvl = 0;
		String dataType = property.substring(property.indexOf("(") + 1, property.indexOf(")"));
		for (TableItem i : dt.getItems()) {
			if (i.getText(0).equals(dataType)) {
				try {
					lvl = Integer.parseInt(i.getText(1));
				} catch (NumberFormatException e) {
					// DEBUG
					System.out.println("Not a number!");
					return -1;
				}
				break;
			}
		}
		return lvl;
	}

	/**
	 * Helper method that calculates the number of trues in a boolean array.
	 * @param bs	the boolean array
	 * @return
	 * 				the number of occurrences of 'true'
	 */
	private static int boolSum(boolean[] bs) {
		// TODO Auto-generated method stub
		int sum = 0;
		for (boolean b : bs) {
			if (b) {
				++sum;
			}
		}
		return sum;
	}

	/**
	 * Method to open a new shell with a diagram representing the modeled
	 * architecture.
	 */
	public static void showDiagram() {
		// some initializing
		Architecture arch2draw = archFunc.getArch();
		final Shell shell = new Shell(display);
		shell.setMaximized(true);
		shell.setText("Architecture Diagram");
		final LightweightSystem lws = new LightweightSystem(shell);
		Figure contents = new Figure();
		XYLayout contentsLayout = new XYLayout();
		// alternative layout
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setHorizontal(true);
		//flowLayout.setMinorAlignment(FlowLayout.ALIGN_LEFTTOP);
		flowLayout.setMinorSpacing(1000); //TODO test
		flowLayout.setMajorSpacing(1000); //TODO test
		flowLayout.setMajorAlignment(FlowLayout.ALIGN_LEFTTOP);
		org.eclipse.draw2d.GridLayout gridLayout = new org.eclipse.draw2d.GridLayout(3, false);
		gridLayout.horizontalSpacing = 460;
		gridLayout.verticalSpacing = 120;
		contents.setLayoutManager(gridLayout);
		// get bounds
		org.eclipse.swt.graphics.Rectangle shellBounds = display.getBounds();
		Monitor primary = display.getPrimaryMonitor();
		flowLayout.setMajorSpacing(primary.getBounds().height / 4);
		flowLayout.setMinorSpacing(primary.getBounds().width / 6);
		if (arch2draw.getCompList().size() > 4) { 
			//contents.setLayoutManager(flowLayout); //TODO test
		} else {
			//contents.setLayoutManager(contentsLayout); //TODO test
		}

		Font classFont = new Font(null, "Arial", 12, SWT.BOLD);
		Font regularFont = new Font(null, "Arial", 10, SWT.NONE);

		// go through architecture and add components and relations
		List<ComponentFigure> components = new ArrayList<ComponentFigure>();
		for (Component c : arch2draw.getCompList()) {
			// create a class object for the component
			org.eclipse.draw2d.Label classLabel = new org.eclipse.draw2d.Label(c.getName());
			classLabel.setFont(classFont);
			final ComponentFigure classFigure = new ComponentFigure(classLabel);
			// add labels for each action
			for (Action a : c.getActions()) {
				org.eclipse.draw2d.Label action = new org.eclipse.draw2d.Label(a.toString());
				classFigure.getMethodsCompartment().add(action);
			}
			contents.add(classFigure);
			components.add(classFigure);
		}

		// fixed positions
		// TODO more than 4?
		//DEBUG
		System.out.println("Display bounds: " + shellBounds.width + ", " + shellBounds.height);
		System.out.println(
				"Primary monitor bounds: " + primary.getBounds().width + ", " + primary.getBounds().height);
		int right = primary.getBounds().width - 850; // 500
		int bottom = primary.getBounds().height - 350; // - 350
		int topLeft = 50; // 100
		Rectangle rectTl = new Rectangle(topLeft, topLeft, -1, -1);
		Rectangle rectTr = new Rectangle(right, topLeft, -1, -1);
		Rectangle rectBl = new Rectangle(topLeft, bottom, -1, -1);
		Rectangle rectBr = new Rectangle(right, bottom, -1, -1);
		if (arch2draw.getCompList().size() == 2) {
			contentsLayout.setConstraint(components.get(0), rectTl);
			contentsLayout.setConstraint(components.get(1), rectTr);
		} else if (arch2draw.getCompList().size() == 3) {
			contentsLayout.setConstraint(components.get(0), rectTl);
			contentsLayout.setConstraint(components.get(1), rectTr);
			contentsLayout.setConstraint(components.get(2), rectBl);
		} else if (arch2draw.getCompList().size() == 4) {
			contentsLayout.setConstraint(components.get(0), rectTl);
			contentsLayout.setConstraint(components.get(1), rectTr);
			contentsLayout.setConstraint(components.get(2), rectBl);
			contentsLayout.setConstraint(components.get(3), rectBr);
		}

		// go through the inter-component actions and add the connections
		List<PolylineConnection> connections = new ArrayList<PolylineConnection>();
		for (Action a : arch2draw.getInterComp_Actions()) {
			Component c1 = null;
			Component c2 = null;
			switch (a.getAction()) {
			case RECEIVE:
				// fall through
			case SPOTCHECK:
				// fall through
			case CRECEIVE:
				// fall through
			case PRECEIVE:
				// source and destination have to be defined
				c1 = a.getComPartner();
				c2 = a.getComponent();
				break;
			default:
				// should not happen
				break;
			}
			PolylineConnection connection = new PolylineConnection();
			connection.setFont(regularFont);
			connection.setLineWidth(2);
			connection.setConnectionRouter(new FanRouter());
			connection.setSourceAnchor(
					new ChopboxAnchor(components.get(new ArrayList<Component>(arch2draw.getCompList()).indexOf(c1))));
			connection.setTargetAnchor(
					new ChopboxAnchor(components.get(new ArrayList<Component>(arch2draw.getCompList()).indexOf(c2))));
			// adding the arrow-head
			PolygonDecoration arrow = new PolygonDecoration();
			arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
			arrow.setScale(20, 10);
			connection.setTargetDecoration(arrow);
			// adding the description
			ConnectionEndpointLocator relationshipLocator2 = new ConnectionEndpointLocator(
					connection, true);
			org.eclipse.draw2d.Label relationshipLabel2 = new org.eclipse.draw2d.Label(a.toString());
			// check if already a connection exists
			PolylineConnection oldConnection = existsConnection(connection, connections);
			if (oldConnection != null && oldConnection.getChildren().size() > 1) {
				// add new label to existing one
				String oldText = ((org.eclipse.draw2d.Label) oldConnection.getChildren().get(1)).getText();
				((org.eclipse.draw2d.Label) oldConnection.getChildren().get(1)).setText(oldText + System.lineSeparator() + a.toString());
			} else {
				connection.add(relationshipLabel2, relationshipLocator2);
				// put the finished connection onto the plane
				contents.add(connection);
				connections.add(connection);
			}
		}
		// also add trust relations
		for (Trust trust : arch2draw.getTrusts()) {
			PolylineConnection connection = new PolylineConnection();
			connection.setLineWidth(2);
			connection.setFont(regularFont);
			connection.setConnectionRouter(new FanRouter());
			connection.setSourceAnchor(
					new ChopboxAnchor(components.get(new ArrayList<Component>(arch2draw.getCompList()).indexOf(trust.getTruster()))));
			connection.setTargetAnchor(
					new ChopboxAnchor(components.get(new ArrayList<Component>(arch2draw.getCompList()).indexOf(trust.getTrustee()))));
			// adding the arrow-head
			PolygonDecoration arrow = new PolygonDecoration();
			arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
			arrow.setScale(20, 10);
			connection.setTargetDecoration(arrow);
			// adding the description
			MidpointLocator relationshipLocator = new MidpointLocator(connection, 0);
			org.eclipse.draw2d.Label relationshipLabel = new org.eclipse.draw2d.Label(trust.toString());
			connection.add(relationshipLabel, relationshipLocator);
			// put the finished connection onto the plane
			contents.add(connection);
		}
		// finally, add the composition relations
		for (Composition compos : arch2draw.getCompositions()) {
			PolylineConnection connection = new PolylineConnection();
			connection.setLineWidth(2);
			connection.setFont(regularFont);
			connection.setConnectionRouter(new FanRouter());
			connection.setSourceAnchor(
					new ChopboxAnchor(components.get(new ArrayList<Component>(arch2draw.getCompList()).indexOf(compos.getComponent()))));
			connection.setTargetAnchor(
					new ChopboxAnchor(components.get(new ArrayList<Component>(arch2draw.getCompList()).indexOf(compos.getContainer()))));
			// adding the arrow-head and diamond
			PolygonDecoration diamond = new PolygonDecoration();
			PointList decorationPointList = new PointList();
			decorationPointList.addPoint(0,0);
			decorationPointList.addPoint(-2,2);
			decorationPointList.addPoint(-4,0);
			decorationPointList.addPoint(-2,-2);
			diamond.setTemplate(decorationPointList);
			connection.setTargetDecoration(diamond);
			PolygonDecoration arrow = new PolygonDecoration();
			arrow.setTemplate(PolygonDecoration.TRIANGLE_TIP);
			arrow.setScale(10, 5);
			connection.setSourceDecoration(arrow);
			// put the finished connection onto the plane
			contents.add(connection);
		}

		lws.setContents(contents);
		shell.open();
		// main loop
		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Method to check if a connection already exists between two components.
	 * 
	 * @param connection
	 *          the new connection
	 * @param connections
	 *          the existing connections
	 * @return the match if found, null else
	 */
	private static PolylineConnection existsConnection(PolylineConnection connection,
			List<PolylineConnection> connections) {
		// go through all existing connections end check if there already is one
		// with the same start and end
		for (PolylineConnection currentConnection : connections) {
			if (currentConnection.getSourceAnchor().getOwner().equals(
					connection.getSourceAnchor().getOwner()) && currentConnection.getTargetAnchor()
					.getOwner().equals(connection.getTargetAnchor().getOwner())) {
				// TODO DEBUG
				System.out.println("found");
				// return the existing connection
				return currentConnection;
			}
		}
		// no existing connection found
		return null;
	}

	/**
	 * Method for user directed output and logs like verification traces.
	 * 
	 * @param type
	 *          the type of message
	 * @param message
	 *          the message itself
	 * @return false, if the message box was canceled
	 */
	public static boolean showMessage(MessageType type, String message) {
		switch (type) {
		case ERR:
			MessageBox errBox = new MessageBox(
					display.getActiveShell(), SWT.ICON_WARNING | SWT.ABORT | SWT.RETRY | SWT.IGNORE);
			errBox.setText("Error");
			errBox.setMessage(message + "If you click abort, the architecture will be reset. abort will let you continue.");
			int buttonId = errBox.open();
			switch (buttonId) {
			case SWT.RETRY:
				reset();
				return false;
			case SWT.IGNORE:
				// do nothing
				return true;
			default:
				break;
			}
			break;
		case INF:
			MessageBox infBox = new MessageBox(display.getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
			infBox.setText("Info");
			infBox.setMessage(message);
			infBox.open();
			return true;
		case LOG:
			// open a new dialog to show a scollable text field
			Shell dialog = new Shell(display.getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			dialog.setMaximized(true);
			dialog.setText("Trace");
			dialog.setLayout(new FillLayout());

			Composite comp = new Composite(dialog, SWT.NONE);
			comp.setLayout(new GridLayout(2, true));
			comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

			Text trace = new Text(
					comp, SWT.MULTI | SWT.READ_ONLY | SWT.LEFT | SWT.V_SCROLL | SWT.H_SCROLL);
			trace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			trace.setText(message);
			trace.setToolTipText("The verification trace of the selected property.");

			Text rules = new Text(
					comp, SWT.MULTI | SWT.READ_ONLY | SWT.LEFT | SWT.V_SCROLL | SWT.H_SCROLL);
			rules.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			try {
				rules.setText(FileReader.readFile("./configs/roi.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			rules.setToolTipText("The rules of inference.");

			dialog.open();
			// main loop
			while (!dialog.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			return true;
		case WARN:
			MessageBox warnBox = new MessageBox(
					display.getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			warnBox.setText("Warning");
			warnBox.setMessage(message);
			int btnId = warnBox.open();
			switch (btnId) {
			case SWT.OK:
				// continue with reset
				System.out.println("OK selected");
				return true;
			case SWT.CANCEL:
				// do nothing
				System.out.println("Cancel selected");
				return false;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * Helper method that calls the {@link ArchitectureFunctions#showTrace(String) showTrace(String)}
	 * method for the selected proof.
	 * @param verifiedProps
	 *          the table with the proof items
	 */
	private void showTrace(Table verifiedProps) {
		// show the verification trace of a property
		for (TableItem i : verifiedProps.getItems()) {
			if (i.getChecked()) {
				archFunc.showTrace(i.getText());
			}
		}
	}

	/**
	 * Helper method that calls the {@link ArchitectureFunctions#verify(String) verify(String)}
	 * method for the selected property. Also updates the table for verified properties.
	 * @param property
	 *          the name of the property to verify
	 * @param verifiedProps
	 *          the table with the verified property items
	 */
	private void verifyProp(String property, Table verifiedProps) {
		// verify the property
		if (archFunc.verify(property)) {
			// property successfully verified
			TableItem item = new TableItem(verifiedProps, SWT.NONE);
			item.setText("[holds] " + property);
		} else {
			// property not verified
			TableItem item = new TableItem(verifiedProps, SWT.NONE);
			item.setText("[does not hold] " + property);
		}
	}

	/**
	 * Helper method that handles the selection via checkboxes.
	 * @param e
	 *          the event
	 * @param verifiedProps
	 *          the table to update
	 */
	private void updateVerifTab(Event e, Table verifiedProps) {
		// handle the selection and deselection of table items
		if (e.detail == SWT.CHECK) {
			for (TableItem i : verifiedProps.getItems()) {
				if (i != e.item) {
					// only one item should be checked at a time
					i.setChecked(false);
				}
			}
		}
	}

	private void updatePurpsTab(Table purps) {
		purps.removeAll();
		for (Purpose p : archFunc.getpuSet()) {
			TableItem item = new TableItem(purps, SWT.NONE);
			item.setText(p.toString());
		}
	}

	private void updatePurps(Combo purp) {
		// TODO test
		purp.removeAll();
		for (Purpose p : archFunc.getpuSet()) {
			purp.add(p.toString());
		}
		purp.setListVisible(true);
	}

	private void updateDataTypes(Combo dt) {
		// TODO Auto-generated method stub
		dt.removeAll();
		for (DataType d : archFunc.getdtSet()) {
			dt.add(d.toString());
		}
		dt.setVisible(true);
	}

	private void updateDataTypesTab(Table dtTable) {
		// TODO Auto-generated method stub
		dtTable.removeAll();
		for (DataType d : archFunc.getdtSet()) {
			TableItem item = new TableItem(dtTable, SWT.NONE);
			item.setText(d.toString());
		}		
	}

	/**
	 * Helper method that syncs a combo with the list of terms
	 * {@link ArchitectureFunctions#gettList() tList}.
	 * @param term
	 *          the term combo
	 */
	private void updateTerms(Combo term) {
		//DEBUG
		System.out.println("UpdateTerms was entered... Term: " + term.getText() + " Terms: " + archFunc.gettSet());
		term.removeAll();
		for (Term t : archFunc.gettSet()) {
			term.add(t.toString());
		}
		term.setListVisible(true);
		//DEBUG
		System.out.println("UpdateTerms was exited...");
	}

	/**
	 * Helper method that syncs a combo with the list of equations
	 * {@link ArchitectureFunctions#geteList() eList}.
	 * @param eq
	 *          the equation combo
	 */
	private void updateEquations(Combo eq) {
		eq.removeAll();
		for (Equation e : archFunc.geteSet()) {
			eq.add(e.toString());
		}
		eq.setListVisible(true);
	}

	/**
	 * Helper method that syncs a table with the list of equations
	 * {@link ArchitectureFunctions#geteList() eList}.
	 * @param eq
	 *          the equation table
	 */
	private void updateEquationsTab(Table eq) {
		eq.removeAll();
		for (Equation e : archFunc.geteSet()) {
			TableItem item = new TableItem(eq, SWT.NONE);
			item.setText(e.toString());
		}
	}

	/**
	 * Helper method that syncs a combo with the list of components
	 * {@link ArchitectureFunctions#getcList() cList}.
	 * @param comp
	 *          the component combo
	 */
	private void updateComponents(Combo comp) {
		comp.removeAll();
		for (Component c : archFunc.getcSet()) {
			comp.add(c.toString());
		}
		comp.setListVisible(true);
	}

	/**
	 * Helper method that syncs a table with the list of components
	 * {@link ArchitectureFunctions#getcList() cList}.
	 * @param compTable
	 *          the component table
	 */
	private void updateCompsTab(Table compTable) {
		compTable.removeAll();
		for (Component c : archFunc.getcSet()) {
			TableItem item = new TableItem(compTable, SWT.NONE);
			item.setText(c.toString());
		}
	}

	/**
	 * Helper method that syncs a table with the list of trust relations
	 * {@link ArchitectureFunctions#gettrustList() trustList}.
	 * @param trustTable
	 *          the trust relations table
	 */
	private void updateTrustTab(Table trustTable) {
		trustTable.removeAll();
		for (Trust t : archFunc.gettrustSet()) {
			TableItem item = new TableItem(trustTable, SWT.NONE);
			item.setText(t.toString());
		}
	}

	/**
	 * Helper method that syncs a table with the list of statements
	 * {@link ArchitectureFunctions#getstList() stList}.
	 * @param stmtTable
	 *          the statement table
	 */
	private void updateStatementTab(Table stmtTable) {
		stmtTable.removeAll();
		for (Statement s : archFunc.getstSet()) {
			TableItem item = new TableItem(stmtTable, SWT.NONE);
			item.setText(s.toString());
		}
	}

	/**
	 * Helper method that syncs a combo with the list of statements
	 * {@link ArchitectureFunctions#getstList() stList}.
	 * @param proofs
	 *          the proof combo
	 */
	private void updateProofs(Combo proofs) {
		proofs.removeAll();
		for (Statement s : archFunc.getstSet()) {
			if (s instanceof Proof) {
				proofs.add(s.toString());
			}
		}
		proofs.setListVisible(true);
	}

	/**
	 * Helper method that syncs a combo with the list of statements
	 * {@link ArchitectureFunctions#getstList() stList}.
	 * @param attests
	 *          the attest combo
	 */
	private void updateAttests(Combo attests) {
		attests.removeAll();
		for (Statement s : archFunc.getstSet()) {
			if (s instanceof Attest) {
				attests.add(s.toString());
			}
		}
		attests.setListVisible(true);
	}

	/**
	 * Helper method that syncs a table with the list of events
	 * {@link ArchitectureFunctions#getaList() aList}.
	 * @param actTable
	 *          the actions table
	 */
	private void updateActionsTab(Table actTable) {
		actTable.removeAll();
		for (Action a : archFunc.getaSet()) {
			TableItem item = new TableItem(actTable, SWT.NONE);
			item.setText(a.toString());
		}
	}

	/**
	 * Helper method that syncs a table with the list of dependence relations
	 * {@link ArchitectureFunctions#getdList() dList}.
	 * @param depTable
	 *          the dependence relations table
	 */
	private void updateDepsTab(Table depTable) {
		depTable.removeAll();
		for (DependenceRelation d : archFunc.getdSet()) {
			TableItem item = new TableItem(depTable, SWT.NONE);
			item.setText(d.toString());
		}
	}

	/**
	 * Helper method that syncs a table with the list of deduction capabilities
	 * {@link ArchitectureFunctions#getdedList() dedList}.
	 * @param dedTable
	 *          the deduction capabilities table
	 */
	private void updateDedsTab(Table dedTable) {
		dedTable.removeAll();
		for (DeductionCapability d : archFunc.getdedSet()) {
			TableItem item = new TableItem(dedTable, SWT.NONE);
			item.setText(d.toString());
		}
	}

	/**
	 * Helper method that syncs a combo with the list of properties
	 * {@link ArchitectureFunctions#getpList() pList}.
	 * @param prop
	 *          the properties combo
	 */
	private void updateProps(Combo prop) {
		prop.removeAll();
		for (Property p : archFunc.getpSet()) {
			prop.add(p.toString());
		}
		prop.setListVisible(true);
	}

	/**
	 * Helper method that syncs a combo with the list of variables
	 * {@link ArchitectureFunctions#getvList() vList}.
	 * @param var
	 *          the variables combo
	 */
	private void updateVariables(Combo var) {
		var.removeAll();
		for (Variable v : archFunc.getvSet()) {
			var.add(v.toString());
		}
		var.setListVisible(true);
	}

	/**
	 * Helper method that syncs a table with the list of variables
	 * {@link ArchitectureFunctions#getvList() vList}.
	 * @param var
	 *          the variables table
	 */
	private void updateVarsTab(Table var) {
		var.removeAll();
		for (Variable v : archFunc.getvSet()) {
			TableItem item = new TableItem(var, SWT.None);
			item.setText(v.toString());
		}
	}

	/**
	 * Helper method that syncs a table with the list of terms
	 * {@link ArchitectureFunctions#gettList() tList}.
	 * @param term
	 *          the terms table
	 */
	private void updateTermsTab(Table term) {
		//DEBUG
		System.out.println("Update TermsTable was started...");
		term.removeAll();
		for (Term t : archFunc.gettSet()) {
			TableItem item = new TableItem(term, SWT.None);
			item.setText(t.toString());
		}
		//DEBUG
		System.out.println("Update TermsTable was finished...");
	}

	/**
	 * Helper method that syncs a table with the list of properties
	 * {@link ArchitectureFunctions#getpList() pList}.
	 * @param propTable
	 *          the properties table
	 */
	private void updatePropsTab(Table propTable) {
		propTable.removeAll();
		for (Property p : archFunc.getpSet()) {
			TableItem item = new TableItem(propTable, SWT.None);
			item.setText(p.toString());
		}
	}

	/**
	 * Helper method that syncs a table with the list of deductions
	 * {@link ArchitectureFunctions#getDeducs() deducs}.
	 * @param dedTable
	 *          the deductions table
	 */
	private void updateDedTab(Table dedTable) {
		dedTable.removeAll();
		for (Deduction d : archFunc.getDeducs()) {
			TableItem item = new TableItem(dedTable, SWT.None);
			item.setText(d.toString());
		}
	}

	/**
	 * Helper method that fills a combo with the operators depending on the state of a button.
	 * @param operator
	 *          the operator combo
	 * @param binary
	 *          the button
	 */
	private void updateOperators(Combo operator, Button binary) {
		//DEBUG
		System.out.println("UpdateOperators was entered...");
		operator.removeAll();
		operator.add("FUNC");
		if (binary.getSelection()) {
			operator.add("ADD");
			operator.add("SUB");
			operator.add("MULT");
			operator.add("DIV");
		}
		operator.setListVisible(true);
		//DEBUG
		System.out.println("UpdateOperators was exited...");
	}

	/**
	 * Helper method that syncs a table with the list of attestations that are
	 * a subet of {@link ArchitectureFunctions#getstList() stList}.
	 * @param att
	 *          the attestations table
	 */
	private void updateAttestsTab(Table att) {
		att.removeAll();
		for (Statement st : archFunc.getstSet()) {
			if (st instanceof Attest) {
				TableItem item = new TableItem(att, SWT.NONE);
				item.setText(st.toString());
			}
		}
	}

	/**
	 * Helper method that syncs a combo with the list of statements
	 * {@link ArchitectureFunctions#getstList() stList}.
	 * @param stTab
	 *          the statements table
	 */
	private void updateStatementsTab(Table stTab) {
		stTab.removeAll();
		for (Statement st : archFunc.getstSet()) {
			TableItem item = new TableItem(stTab, SWT.NONE);
			item.setText(st.toString());
		}
	}

	/**
	 * Helper method that handles the removal of checked items from a table.
	 * @param type
	 *          the type of object like variable or statement
	 * @param table
	 *          the table to remove the items from
	 */
	private void handleRemove(ObjectType type, Table table) {
		// remove items from a table
		for (TableItem i : table.getItems()) {
			if (i.getChecked()) {
				switch (type) {
				case ACT:
					archFunc.removeAction(i.getText());
					break;
				case COMP:
					archFunc.removeComponent(i.getText());
					break;
				case EQ:
					archFunc.removeEquation(i.getText());
					break;
				case STMT:
					archFunc.removeStatement(i.getText());
					break;
				case TERM:
					archFunc.removeTerm(i.getText());
					break;
				case TRUST:
					archFunc.removeTrust(i.getText());
					break;
				case VAR:
					archFunc.removeVariable(i.getText());
					break;
				case DED:
					archFunc.removeDed(i.getText());
					break;
				case DEP:
					archFunc.removeDep(i.getText());
					break;
				case PROP:
					archFunc.removeProp(i.getText());
					break;
				case PURP:
					archFunc.removePurp(i.getText());
					break;
				case DT:
					archFunc.removeDt(i.getText());
					break;
				default:
					// nothing
					break;
				}
				table.remove(table.indexOf(i));
			}
		}
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addTerm(OperatorType, Operator, String, String, String, String)
	 * addTerm(OperatorType, Operator, String, String, String, String)} with the correct input
	 * from the combos.
	 * @param unary
	 *          the radio button option unary
	 * @param binary
	 *          the radio button option binary
	 * @param operator
	 *          the operator combo
	 * @param funcName
	 *          the 'name of the function' text field
	 * @param term1
	 *          the first term combo
	 * @param term2
	 *          the second term combo
	 * @param term3
	 *          the third term combo
	 */
	private void handleTerms(Button unary, Button binary, Combo operator,
			Text funcName, Combo term1, Combo term2, Combo term3) {
		//DEBUG
		System.out.println("HandleTerms was entered..." + term1.getText() + " Selection: " + term1.getSelectionIndex());
		// prepare the right data types
		OperatorType opType = unary.getSelection() ? OperatorType.UNARY
				: (binary.getSelection() ? OperatorType.BINARY : OperatorType.TERTIARY);
		if (operator.getText().equals("")) {
			// no item selected
			return;
		}
		Operator op = Operator.valueOf(operator.getText());
		String fctName = funcName.isEnabled() ? funcName.getText() : null;
		String t1 = term1.getText();
		String t2 = term2.isEnabled() ? term2.getText() : null;
		String t3 = term3.isEnabled() ? term3.getText() : null;
		// create the term and add it to the list
		archFunc.addTerm(opType, op, fctName, t1, t2, t3);
		//DEBUG
		System.out.println("HandleTerms was exited...");
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addEquation(String, Type, String, String, String, String)
	 * addEquations(String, Type, String, String, String, String)} with the correct input
	 * from the combos.
	 * @param conjunc
	 *          the radio button option conjunction
	 * @param eqName
	 *          the 'name of the equation' text field
	 * @param e1
	 *          the first equation combo
	 * @param e2
	 *          the second equation combo
	 * @param t1
	 *          the first term combo
	 * @param t2
	 *          the second term combo
	 */
	private void handleEquations(Button conjunc, Text eqName,
			Combo e1, Combo e2, Combo t1, Combo t2) {
		// prepare the right data types
		Type type = conjunc.getSelection() ? Type.CONJUNCTION : Type.RELATION;
		String eq1 = e1.getText();
		String eq2 = e2.getText();
		String term1 = t1.getText();
		String term2 = t2.getText();
		// create the equation and add it to the list
		archFunc.addEquation(eqName.getText(), type, eq1, eq2, term1, term2);
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addReceive(String, String, List, List)
	 * addReceive(String, String, List, List)} with the correct input from the combos.
	 * @param comp1
	 *          the first component combo
	 * @param comp2
	 *          the second component combo
	 * @param stTable
	 *          the table containing the statements
	 * @param varTable
	 *          the table containing the variables
	 */
	private void handleReceive(Combo comp1, Combo comp2, Table stTable, Table varTable) {
		// prepare the right data types
		Set<String> stSet = new LinkedHashSet<String>();
		Set<String> varSet = new LinkedHashSet<String>();
		for (TableItem i : stTable.getItems()) {
			if (i.getChecked()) {
				stSet.add(i.getText());
			}
		}
		for (TableItem i : varTable.getItems()) {
			if (i.getChecked()) {
				varSet.add(i.getText());
			}
		}
		// create the receive action and add it to the list
		archFunc.addReceive(comp1.getText(), comp2.getText(), stSet, varSet);
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addPReceive(String, String, String, List)
	 * addPReceive(String, String, String, List)} with the correct input from the combos.
	 * @param comp1
	 * 			the first component combo
	 * @param comp2
	 * 			the second component combo
	 * @param purp
	 * 			the purpose combo
	 * @param varTable
	 * 			the variables table
	 */
	private void handlePReceive(Combo comp1, Combo comp2, Combo purp, Table varTable) {
		// TODO Auto-generated method stub
		// prepare the right data types
		Set<String> varSet = new LinkedHashSet<String>();
		for (TableItem i : varTable.getItems()) {
			if (i.getChecked()) {
				varSet.add(i.getText());
			}
		}
		// create the preceive action and add it to the list
		archFunc.addPReceive(comp1.getText(), comp2.getText(), purp.getText(), varSet);
	}
	
	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addCReceive(String, String, String, Set)
	 * addCReceive(String, String, String, Set)} with the correct input from the combos.
	 * @param comp1
	 * 			the first component combo
	 * @param comp2
	 * 			the second component combo
	 * @param dt
	 * 			the data type combo
	 * @param varTable
	 * 			the variables table
	 */
	private void handleCReceive(Combo comp1, Combo comp2, Combo dt, Table varTable) {
		// TODO Auto-generated method stub
		// prepare the right data types
		Set<String> varSet = new LinkedHashSet<String>();
		for (TableItem i : varTable.getItems()) {
			if (i.getChecked()) {
				varSet.add(i.getText());
			}
		}
		// create the creceive action and add it to the list
		archFunc.addCReceive(comp1.getText(), comp2.getText(), dt.getText(), varSet);
	}

	/**
	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addPurpose(String, Set, Set, Set) addPurpose(String, Set, Set, Set)}
	 * with the correct input from the combos.
	 * @param purpName
	 * 			the name of the purpose
	 * @param vars1
	 * 			the variables table
	 * @param purps1
	 * 			the parent purposes
	 * @param purps2
	 * 			the child purposes
	 */
	private void handlePurpose(Text purpName, Table vars1, Table purps1, Table purps2) {
		// TODO Auto-generated method stub
		// prepare the right data types
		Set<String> varSet = new LinkedHashSet<String>();
		Set<String> purpSet1 = new LinkedHashSet<String>();
		Set<String> purpSet2 = new LinkedHashSet<String>();
		for (TableItem i : vars1.getItems()) {
			if (i.getChecked()) {
				varSet.add(i.getText());
			}
		}
		for (TableItem i : purps1.getItems()) {
			if (i.getChecked()) {
				purpSet1.add(i.getText());
			}
		}
		for (TableItem i : purps2.getItems()) {
			if (i.getChecked()) {
				purpSet2.add(i.getText());
			}
		}
		// create the purpose and add it to the list
		// also update the purpose hierarchy
		archFunc.addPurpose(purpName.getText(), varSet, purpSet1, purpSet2);
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addDataType(String, Set) addDataType(String, Set)}
	 * with the correct input from the combos.
	 * @param dtName
	 * @param vars2
	 */
	private void handleDt(Text dtName, Table vars2) {
		// TODO Auto-generated method stub
		// prepare the data
		Set<String> varSet = new LinkedHashSet<String>();
		for (TableItem i : vars2.getItems()) {
			if (i.getChecked()) {
				varSet.add(i.getText());
			}
		}
		// create the data type and add it to the list
		archFunc.addDataType(dtName.getText(), varSet);
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addCheck(String, Set) addCheck(String, Set)}
	 * with the correct input from the combos.
	 * @param comp
	 *          the component combo
	 * @param eqTable
	 *          the equations table
	 */
	private void handleCheck(Combo comp, Table eqTable) {
		// prepare the right data types
		Set<String> eqSet = new LinkedHashSet<String>();
		for (TableItem i : eqTable.getItems()) {
			if (i.getChecked()) {
				eqSet.add(i.getText());
			}
		}
		// create the check action and add it to the list
		archFunc.addCheck(comp.getText(), eqSet);
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addVerify(String, String, boolean)
	 * addVerify(String, String, boolean)} with the correct input from the combos.
	 * @param att
	 *          the radio button that indicates type of statement
	 * @param comp
	 *          the component combo
	 * @param pList
	 *          the proof combo
	 * @param aList
	 *          the attest combo
	 */
	private void handleVerify(Button att, Combo comp, Combo pList, Combo aList) {
		if (att.getSelection()) {
			archFunc.addVerify(comp.getText(), aList.getText(), false);
		} else {
			archFunc.addVerify(comp.getText(), pList.getText(), true);
		}
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addDep(String, String, List) addDep(String, String, List)}
	 * with the correct input from the combos.
	 * @param comp
	 *          the component combo
	 * @param var
	 *          the variable combo
	 * @param varTable
	 *          the variables table
	 */
	private void handleDep(Combo comp, Combo var, Table varTable, Text prob) {
		// prepare the right data types
		Set<String> varSet = new LinkedHashSet<String>();
		for (TableItem i : varTable.getItems()) {
			if (i.getChecked()) {
				varSet.add(i.getText());
			}
		}
		// create the dependence relation and add it to the list
		archFunc.addDep(comp.getText(), var.getText(), varSet, prob.getText());
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addDed(String, List) addDed(String, List)}
	 * with the correct input from the combos.
	 * @param comp
	 *          the component combo
	 * @param dedTable
	 *          the deductions table
	 */
	private void handleDed(Combo comp, Table dedTable) {
		// prepare the right data types
		Set<String> dedSet = new LinkedHashSet<String>();
		for (TableItem i : dedTable.getItems()) {
			if (i.getChecked()) {
				dedSet.add(i.getText());
			}
		}
		// create the deduction and add it to the list
		archFunc.addDed(comp.getText(), dedSet);
	}

	/**
	 * Helper method that calls the method
	 * {@link ArchitectureFunctions#addDeduc(String, List, String) addDeduc(String, List, String)}
	 * with the correct input from the combos.
	 * @param dedName
	 *          the 'name of the deduction' text
	 * @param premiseTable
	 *          the premises table
	 * @param conclusion
	 *          the conclusion combo
	 */
	private void handlemyDed(Text dedName, Table premiseTable, Combo conclusion, Text prob) {
		// prepare the right data types
		Set<String> eqSet = new LinkedHashSet<String>();
		for (TableItem i : premiseTable.getItems()) {
			if (i.getChecked()) {
				eqSet.add(i.getText());
			}
		}
		// create the check action and add it to the list
		archFunc.addDeduc(dedName.getText(), eqSet, conclusion.getText(), prob.getText());
	}

	/**
	 * Helper method that either calls {@link ArchitectureFunctions#addAttest(String, List)
	 * addAttest(String, List)} or {@link ArchitectureFunctions#addProof(String, List)
	 * addProof(String, List)} depending on the type of statement.
	 * @param att
	 *          the radio button option attestation
	 * @param comp
	 *          the component combo
	 * @param eqTable
	 *          the equations table
	 * @param attTable
	 *          the attestations table
	 */
	private void handleStatement(Button att, Combo comp, Table eqTable, Table attTable) {
		// prepare the right data types
		Set<String> pSet = new LinkedHashSet<String>();
		for (TableItem i : eqTable.getItems()) {
			if (i.getChecked()) {
				pSet.add(i.getText());
			}
		}
		for (TableItem i : attTable.getItems()) {
			if (i.getChecked()) {
				pSet.add(i.getText());
			}
		}
		if (att.getSelection()) {
			archFunc.addAttest(comp.getText(), pSet);
		} else {
			archFunc.addProof(comp.getText(), pSet);
		}
	}
}