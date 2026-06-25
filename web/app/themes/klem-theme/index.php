<?php get_header(); ?>

<main class="pt-24 max-w-7xl mx-auto px-6 py-16">
    <?php if (have_posts()) : while (have_posts()) : the_post(); ?>
        <article>
            <h1 class="text-3xl font-bold text-klem-blue"><?php the_title(); ?></h1>
            <div class="mt-4 prose"><?php the_content(); ?></div>
        </article>
    <?php endwhile; endif; ?>
</main>

<?php get_footer(); ?>
