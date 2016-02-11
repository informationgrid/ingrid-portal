(function () {

    "use strict";

    $.fn.selection = function (options) {

        return this.each(function () {

            var activeOption = $(this).find('.selection-options.is-active');
            var options = $(this).find('.selection-options');

            $('#' + this.id).on('change', function (event) {

                event.preventDefault();
                event.stopPropagation();

                if (activeOption == $(this)) {
                    return;
                }
                
                if (activeOption !== null) {
                    $(activeOption).removeClass('is-active');
                    $('.selection-content[for="' + $(activeOption).attr('id') + '"]').addClass('is-hidden');
                }
                
                var id = this.getAttribute('id');
                var selectedOption = $('option:selected', this);
                selectedOption.addClass('is-active');
                $('.selection-content[for="' + $(selectedOption).attr('id') + '"]').removeClass('is-hidden');
                
                activeOption = selectedOption;
            });
        });
    }

})();
