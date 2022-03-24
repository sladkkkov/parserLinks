package com.example.application.views.FTP;

import com.example.application.Link;
import com.example.application.service.LinkService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@PageTitle("Получить ссылки через сокет")
@Route(value = "socketView", layout = MainLayout.class)
public class SocketView extends VerticalLayout {

    public SocketView() {
        HorizontalLayout layout = new HorizontalLayout();
        TextField getLink = new TextField();
        getLink.setLabel("Введите ссылку");
        getLink.setRequiredIndicatorVisible(true);
        getLink.setErrorMessage("Поле пустое");
        TextField size = new TextField();
        size.setRequiredIndicatorVisible(true);
        size.setErrorMessage("Поле пустое");
        size.setLabel("Полный размер сайта");


        Button button = new Button("Начать");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Grid<Link> linksTable = new Grid<>(Link.class, false);
        linksTable.addColumn(Link::getId).setHeader("Номер").setSortable(true).setComparator(Link::getId);
        linksTable.addColumn(Link::getName).setHeader("Ссылка").setSortable(true).setComparator(Link::getName);
        linksTable.addColumn(Link::getSize).setHeader("Размер").setSortable(true).setComparator(Link::getSize);


        button.addClickListener(clickEvent -> {
            try {
                LinkService linkService = new LinkService(getLink.getValue());
                linkService.parseHtml(linkService.getAllLinksBySocket());

                linksTable.setItems(linkService.sortLinks(linkService.getLinksSet()));
                linksTable.setItems(linkService.sortLinks(linkService.parseHtml(linkService.getAllLinksBySocket())));

                size.setValue(String.valueOf(linkService.getSizeSite()));
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        });

        layout.add(getLink, size);
        add(layout, button, linksTable);
    }
}
