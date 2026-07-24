document.addEventListener('DOMContentLoaded', function () {
    var button = document.getElementById('klem-generate-password');
    var field  = document.getElementById('klem-password-field');

    if (!button || !field) {
        return;
    }

    button.addEventListener('click', function () {
        var chars  = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%';
        var values = new Uint32Array(16);
        window.crypto.getRandomValues(values);

        var password = '';
        for (var i = 0; i < values.length; i++) {
            password += chars[values[i] % chars.length];
        }

        field.value = password;
        field.type  = 'text';
    });
});
