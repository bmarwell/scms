scms {
  model {
    year = '2021'
    links = [
        [ name : 'Architecture',
          target : 'architecture.md'
        ],
        [ name : 'Subject',
          target : 'subject.md'
        ],
    ]
  }

  patterns {

    '**/*.md' {
      model {
        year = '1910'
      }
    }

  }
}
