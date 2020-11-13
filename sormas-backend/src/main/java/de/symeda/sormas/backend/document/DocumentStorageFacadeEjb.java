/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.backend.document;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.document.DocumentStorageFacade;

@Stateless(name = "DocumentStorageFacade")
public class DocumentStorageFacadeEjb implements DocumentStorageFacade {

	@EJB
	private DocumentStorageService documentStorageService;
	@EJB
	private DocumentService documentService;

	@Override
	public byte[] read(String uuid) throws IOException {
		Document document = documentService.getByUuid(uuid);
		return documentStorageService.read(document);
	}

	@Override
	public void store(String uuid, byte[] content) throws IOException {
		Document document = documentService.getByUuid(uuid);
		documentStorageService.save(document, content);
	}

	@Override
	public void cleanupDeletedDocuments() {
		List<Document> deleted = documentService.getDeletedDocuments();
		for (Document document : deleted) {
			documentStorageService.delete(document);
			documentService.delete(document);
		}
	}

	@LocalBean
	@Stateless
	public static class DocumentStorageFacadeEjbLocal extends DocumentStorageFacadeEjb {
	}
}
