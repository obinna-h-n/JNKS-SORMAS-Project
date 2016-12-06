package de.symeda.sormas.ui.caze;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractCaseView extends AbstractSubNavigationView {

	private CaseReferenceDto caseRef;
	
	protected AbstractCaseView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, String params) {
		
		caseRef = FacadeProvider.getCaseFacade().getReferenceByUuid(params);
		
		menu.removeAllViews();
		menu.addView(CasesView.VIEW_NAME, "Cases list");
		menu.addView(CaseDataView.VIEW_NAME, I18nProperties.getFieldCaption(CaseDataDto.I18N_PREFIX), params);
		menu.addView(CasePersonView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.PERSON), params);
		menu.addView(CaseSymptomsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.SYMPTOMS), params);
		menu.addView(CaseContactsView.VIEW_NAME, I18nProperties.getPrefixFieldCaption(CaseDataDto.I18N_PREFIX, "contacts"), params);
    }

	public CaseReferenceDto getCaseRef() {
		return caseRef;
	}
}
