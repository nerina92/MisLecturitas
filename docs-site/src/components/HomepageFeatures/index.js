import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: '📚 Biblioteca Digital',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        Accede a una completa biblioteca de libros digitales organizados por niveles
        de dificultad para facilitar el aprendizaje progresivo de la lectura.
      </>
    ),
  },
  {
    title: '🎧 Audiolibros',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        Disfruta de audiolibros narrados profesionalmente que complementan
        la experiencia de lectura y ayudan en la comprensión auditiva.
      </>
    ),
  },
  {
    title: '🔐 Tecnología Moderna',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        Desarrollada con Kotlin, Jetpack Compose y Firebase. Arquitectura MVVM
        para una aplicación robusta, escalable y segura.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
