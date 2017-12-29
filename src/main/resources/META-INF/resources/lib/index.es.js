'use strict';

import Modal from './Modal';

import RestClient from 'another-rest-client';

export default function(elementId) {
    const btn = document.getElementById(elementId);
    const service = new RestClient('/o/metalrest/portlet');
    service.res('hello');

    let modal;

    btn.addEventListener('click', () => {
        if (!modal) {
            modal = new Modal({
                header: 'A Metal.js Modal Dialog',
                body: service.hello.get(),
            });
        } else {
            modal.shown = true;
        }
    });
}