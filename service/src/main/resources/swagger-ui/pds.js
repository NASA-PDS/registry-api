function waitForElement(selector) {
    return new Promise(resolve => {
        if (document.querySelector(selector)) {
            return resolve(document.querySelectorAll(selector));
        }

        const observer = new MutationObserver(function() {
            // const observer = new MutationObserver(mutations => {
           if (document.querySelector(selector)) {
               resolve(document.querySelectorAll(selector));
               observer.disconnect();
           }
        });

        observer.observe(document.querySelector('.swagger-container > .swagger-ui'), { childList: true });
    });
}

function createExternalLinkElement() {
    const link = document.createElement('a');
    link.setAttribute('rel', 'noopener noreferrer');
    return link;
}

async function collapseOpblocks() {
    const openOpblocks = await waitForElement('.opblock-tag-section.is-open');
    for (let opblock of openOpblocks) {
        opblock.querySelector('h3 > button').click();
    }
}

function insertFooter() {
    const footer = document.createElement('div');
    footer.setAttribute('id', 'footer');

    const usaGov = createFooterUsaGov();
    const externalLinks = createFooterExternalLinks();
    const siteInfo = createFooterSiteInfo();
    footer.append(usaGov, externalLinks, siteInfo);
    document.getElementById('swagger-ui').after(footer);
}

function createFooterUsaGov() {
    const usaGov = document.createElement('div');
    usaGov.classList.add('footer-section', 'left');

    const logo = createExternalLinkElement();
    logo.setAttribute('href', 'http://www.usa.gov');
    const logoImg = document.createElement('img');
    logoImg.setAttribute('src', 'https://pds.nasa.gov/images/usa-gov.gif');
    logoImg.setAttribute('alt', 'USA.gov home');
    logoImg.setAttribute('border', '0');
    logo.append(logoImg);

    const links = document.createElement('div');
    links.classList.add('block-info');
    const privacy = createExternalLinkElement();
    privacy.setAttribute('href', 'https://www.nasa.gov/about/highlights/HP_Privacy.html');
    privacy.innerText = 'Privacy / Copyright';
    const freedom = createExternalLinkElement();
    freedom.setAttribute('href', 'http://www.hq.nasa.gov/office/pao/FOIA/');
    freedom.innerText = 'Freedom of Information Act';
    links.append(privacy, freedom);

    usaGov.append(logo, links);
    return usaGov;
}

function createFooterExternalLinks() {
    const externalLinks = document.createElement('div');
    externalLinks.classList.add('footer-section', 'middle');

    const nasa = createExternalLinkElement();
    nasa.setAttribute('href', 'http://www.nasa.gov/');
    nasa.innerText = 'NASA';
    const caltech = createExternalLinkElement();
    caltech.setAttribute('href', 'http://www.caltech.edu/');
    caltech.innerText = 'Caltech';
    const privacy = createExternalLinkElement();
    privacy.setAttribute('href', 'https://www.jpl.nasa.gov/privacy');
    privacy.innerText = 'Privacy';
    const imagePolicy = createExternalLinkElement();
    imagePolicy.setAttribute('href', 'https://www.jpl.nasa.gov/imagepolicy');
    imagePolicy.innerText = 'Image Policy';
    const faq = createExternalLinkElement();
    faq.setAttribute('href', 'https://www.jpl.nasa.gov/faq.php');
    faq.innerText = 'FAQ';
    const feedback = createExternalLinkElement();
    feedback.setAttribute('href', 'https://www.jpl.nasa.gov/contact_JPL.php');
    feedback.innerText = 'Feedback';

    externalLinks.append(nasa, caltech, privacy, imagePolicy, faq, feedback);
    return externalLinks;
}

function createFooterSiteInfo() {
    const siteInfo = document.createElement('div');
    siteInfo.classList.add('footer-section', 'right');

    const logo = document.createElement('img');
    logo.setAttribute('src', '../static/logo.png');
    logo.setAttribute('alt', 'NASA logo');
    logo.setAttribute('width', '75');

    const info = document.createElement('p');
    info.classList.add('block-info');
    info.innerHTML = 'Webmaster: <a href="mailto:pds_operator@jpl.nasa.gov">PDS Operator</a><br />' +
        'NASA Official: <a href="mailto:meagan.thompson@nasa.gov">Meagan Thompson</a><br />' +
        'Clearance: CL#22-1582<br />' +
        'Last updated: March 2022';

    siteInfo.append(logo, info);
    return siteInfo;
}

window.addEventListener('load', (event) => {
    collapseOpblocks();
    insertFooter();
});